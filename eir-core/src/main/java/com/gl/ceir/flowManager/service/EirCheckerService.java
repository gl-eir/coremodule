package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.AlertIds;
import com.gl.ceir.flowManager.contstants.ReasonCode;
import com.gl.ceir.flowManager.dto.DeviceTypeListResponseData;
import com.gl.ceir.flowManager.dto.ListResponseData;
import com.gl.ceir.flowManager.dto.VerificationRequest;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EirCheckerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${domestic-imsie-prefix}")
    private String domesticImsie;

    private String[] localImsiPrefix = null;

    @Autowired
    IConfigFlagService configFlagService;

    @Autowired
    IExceptionListService exceptionListService;

    @Autowired
    IBlackListService blackListService;

    @Autowired
    IBlockedTACListService blockedTACListService;

    @Autowired
    IAllowedTACListService allowedTACListService;

    @Autowired
    IDeviceTypeListService deviceTypeListService;

    @Autowired
    ITrackedListService trackedListService;

    @Autowired
    ModuleAlertService coreModuleAlertService;

    @PostConstruct
    public void init() {
        localImsiPrefix = domesticImsie.split(",");
        logger.info("Domestic domesticImsie:{} localImsiPrefix:{}", domesticImsie, localImsiPrefix);
    }

    public ListResponseData isPresentInExceptionList(VerificationRequest request) {
        ListResponseData responseData = new ListResponseData();
        responseData.setPresent(false);
        if (configFlagService.isExceptionListOn()) {
            try {
                responseData = exceptionListService.isPresent(request);
                responseData.setPresent(true);
            } catch (DataNotFoundException dataNotFoundException) {
                logger.info("Not Found in Exception List request{}", request);
            }
        }
        return responseData;
    }

    public ListResponseData isPresentInBlockedList(VerificationRequest request) {
        ListResponseData responseData = new ListResponseData();
        responseData.setPresent(false);
        if (configFlagService.isBlackListOn()) {
            try {
                responseData = blackListService.isPresent(request);
                responseData.setPresent(true);
            } catch (DataNotFoundException dataNotFoundException) {
                logger.info("Not Found in Block/Black List request{}", request);
            }
        }
        return responseData;
    }


    public boolean isPresentInBlockedTacList(VerificationRequest request) {
        boolean returnVal = false;
        if (configFlagService.isBlockedTacOn()) {
            try {
                blockedTACListService.isPresent(request);
                returnVal = true;
            } catch (DataNotFoundException dataNotFoundException) {
                logger.info("Not Found in Blocked Tack request{}", request);
            }
        }
        return returnVal;
    }

    public boolean isNotPresentInAllowedTacList(VerificationRequest request) {
        return !isPresentInAllowedTacList(request);
    }

    public boolean isPresentInAllowedTacList(VerificationRequest request) {
        boolean returnVal = false;
        if (configFlagService.isAllowedTacOn()) {
            try {
                allowedTACListService.isPresent(request);
                returnVal = true;
            } catch (DataNotFoundException dataNotFoundException) {
                logger.info("Not Found in Allowed Tac request{}", request);
            }
        }
        return returnVal;
    }

    public ListResponseData isPresentInGreyList(VerificationRequest request) {
        ListResponseData responseData = new ListResponseData();
        responseData.setPresent(false);
        if (configFlagService.isTrackedListOn()) {
            try {
                responseData = trackedListService.isPresent(request);
                responseData.setPresent(true);
            } catch (DataNotFoundException dataNotFoundException) {
                logger.info("Not Found in Grey List request{}", request);
            }
        }
        return responseData;
    }

    public DeviceTypeListResponseData isDeviceTypeAllowed(VerificationRequest request) {
        String tac = request.getTac();
        DeviceTypeListResponseData responseData = deviceTypeListService.getDeviceTypeData(tac);
        if (responseData.getDeviceType() == null) {
            responseData.setNotAllowed(true);
            responseData.setReasonCode(ReasonCode.invalidDevice);
            responseData.setStatusValue(configFlagService.getTacNotFoundInGSMAStatus());
        } else {
            if (configFlagService.isDeviceTypeOn(responseData.getDeviceType())) {
                responseData.setNotAllowed(false);
            } else {
                logger.info("Not Allowing to proceed for further check as not found in Device Type(GSMA Tac list) {} DeviceType:{}", request, responseData);
                responseData.setNotAllowed(true);
                responseData.setReasonCode(ReasonCode.blockedDevicetype);
                responseData.setStatusValue(configFlagService.getDeviceTypeOffStatus(responseData.getDeviceType()));
            }
        }
        return responseData;
    }

    public boolean isForeignImsiAllowedAndImsiNull(VerificationRequest request) {
        if (configFlagService.isForeignImsiCheckOn()) {
            if (StringUtils.isBlank(request.getImsi())) {
                coreModuleAlertService.sendImsiNullAlert(request.getTid());
                return true;
            }
        }
        return false;
    }

    public boolean isForeignImsiAllowed(VerificationRequest request) {
        if (configFlagService.isForeignImsiCheckOn()) {
            if (request.getImsi() == null || isForeignImsi(request.getImsi())) {
                return true;
            }
        }
        return false;
    }

    private boolean isForeignImsi(String imsi) {
        if (StringUtils.startsWithAny(imsi, localImsiPrefix)) {
            return false;
        }
        return true;
    }

}
