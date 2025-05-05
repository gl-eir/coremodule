package com.gl.ceir.flowManager.orchestrator;

import com.gl.ceir.flowManager.configuration.InfoMapperUdpServerConfig;
import com.gl.ceir.flowManager.contstants.*;
import com.gl.ceir.flowManager.dto.DeviceTypeListResponseData;
import com.gl.ceir.flowManager.dto.ListResponseData;
import com.gl.ceir.flowManager.dto.VerificationRequest;
import com.gl.ceir.flowManager.dto.VerificationResponse;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import com.gl.ceir.flowManager.exception.ImeiNullException;
import com.gl.ceir.flowManager.server.IUdpSender;
import com.gl.ceir.flowManager.service.ModuleAlertService;
import com.gl.ceir.flowManager.service.EirCheckerService;
import com.gl.ceir.flowManager.service.IConfigFlagService;
import com.gl.ceir.flowManager.service.IHlrDumpService;
import com.gl.ceir.flowManager.traces.EdrDto;
import com.gl.ceir.flowManager.traces.TracesService;
import com.gl.ceir.flowManager.validator.GeneralValidator;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RequestProcessOrchestrator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IConfigFlagService configFlagService;

    @Autowired
    GeneralValidator generalValidator;

    @Autowired
    Gson gson;

    @Autowired
    IUdpSender udpSender;

    @Autowired
    EirCheckerService eirCheckerService;

    @Autowired
    InfoMapperUdpServerConfig infoMapperUdpServerConfig;

    @Autowired
    IHlrDumpService hlrDumpService;

    private final String protocol = "";

    private final String SUCCESS = "success";

    @Autowired
    ModuleAlertService coreModuleAlertService;

    @Autowired
    TracesService tracesService;

    public void processUdpRequest(VerificationRequest verificationRequest) {
        VerificationResponse response = checkRules(verificationRequest);
        udpSender.send(verificationRequest.getResponseIp(), verificationRequest.getResponsePort(), gson.toJson(response));
        if (response.getStatus() == StatusValue.blacklist.getCode()) {
            EdrDto edrDto = new EdrDto("Diameter", response);
            edrDto.setSessionId(verificationRequest.getTid());
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            edrDto.setTimeStamp(timeStamp);
            tracesService.sendBlackListTrace(edrDto);
        } else if (response.getStatus() == StatusValue.greylist.getCode()) {
            EdrDto edrDto = new EdrDto("Diameter", response);
            edrDto.setSessionId(verificationRequest.getTid());
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            edrDto.setTimeStamp(timeStamp);
            tracesService.sendGreyListTrace(edrDto);
        }
    }

    public VerificationResponse checkRules(VerificationRequest request) {
        try {
            request.formatImei();
        } catch (ImeiNullException e) {
            coreModuleAlertService.sendImeiNullAlert(request.getTid());
        }

        if (configFlagService.isWorkFlowGlobalOff()) {
            logger.info("WorkFlowGlobalFlag is Off {}", request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getName(), request.getMsisdnFilled().getCode(), ReasonCode.eirWorkFlowGlobalFlagIsOff.getCode(), protocol, SUCCESS);
        }

        if (eirCheckerService.isForeignImsiAllowedAndImsiNull(request)) {
            logger.info("Imsi is null{}", request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getName(), request.getMsisdnFilled().getCode(), ReasonCode.imsiNull.getCode(), protocol, SUCCESS);
        }

        if (eirCheckerService.isForeignImsiAllowed(request)) {
            logger.info("Foreign Imsi {}", request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getName(), request.getMsisdnFilled().getCode(), ReasonCode.foreignImsi.getCode(), protocol, SUCCESS);
        }

        if (request.getMsisdnFilled() == MsisdnFilledStatus.MISSING && infoMapperUdpServerConfig.getInfoMapperIsEnabled()) {
            try {
                if (StringUtils.isNotBlank(request.getImsi())) {
                    String msisdn = hlrDumpService.getMsisdn(request.getImsi());
                    request.setMsisdn(msisdn);
                    request.setMsisdnFilled(MsisdnFilledStatus.FILLED_FROM_HLR);
                }
            } catch (DataNotFoundException dataNotFoundException) {
                request.setMsisdnFilled(MsisdnFilledStatus.NOT_FOUND_IN_HLR);
            }
        }


        ListResponseData listResponseData = eirCheckerService.isPresentInExceptionList(request);
        if (listResponseData.isPresent()) {
            logger.info("Present in Exception List {}", request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getName(), request.getMsisdnFilled().getCode(), listResponseData.getReasonCode().getCode(), protocol, SUCCESS);
        }

        listResponseData = eirCheckerService.isPresentInBlockedList(request);
        if (listResponseData.isPresent()) {
            logger.info("Present in BlockedList {}", request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), StatusValue.blacklist.getCode(), StatusValue.blacklist.getName(), request.getMsisdnFilled().getCode(), listResponseData.getReasonCode().getCode(), protocol, SUCCESS);
        }

        if (eirCheckerService.isPresentInBlockedTacList(request)) {
            logger.info("Present in Blocked Tac List {}", request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), StatusValue.blacklist.getCode(), StatusValue.blacklist.getName(), request.getMsisdnFilled().getCode(), ReasonCode.blockedByBlockedTacList.getCode(), protocol, SUCCESS);
        }

        listResponseData = eirCheckerService.isPresentInGreyList(request);
        if (listResponseData.isPresent()) {
            logger.info("Present in Grey List {}", request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), StatusValue.greylist.getCode(), StatusValue.greylist.getName(), request.getMsisdnFilled().getCode(), listResponseData.getReasonCode().getCode(), protocol, SUCCESS);
        }

        GeneralValidator.ValidateResponse imeiValidResponse = generalValidator.validateImei(request.getImei());
        if (imeiValidResponse.getIsValid()) {
            if (configFlagService.isDeviceTypeGlobalOn()) {
                if (imeiValidResponse.getIsValidationEnabled()) {
                    DeviceTypeListResponseData deviceTypeListResponse = eirCheckerService.isDeviceTypeAllowed(request);
                    if (deviceTypeListResponse.isNotAllowed()) {
                        Integer deviceType = DeviceType.Others.getCode();
                        if (deviceTypeListResponse.getDeviceType() != null)
                            deviceType = deviceTypeListResponse.getDeviceType().getCode();
                        logger.info("Device Type Validation Reason Code:{} {}", deviceTypeListResponse.getReasonCode().name(), request);
                        return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), deviceType, deviceTypeListResponse.getStatusValue().getCode(), deviceTypeListResponse.getStatusValue().getName(), request.getMsisdnFilled().getCode(), deviceTypeListResponse.getReasonCode().getCode(), protocol, SUCCESS);
                    }
                    logger.info("All check Passed {}", request);
                    return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), deviceTypeListResponse.getDeviceType().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getName(), request.getMsisdnFilled().getCode(), ReasonCode.allCheckPassed.getCode(), protocol, SUCCESS);
                } else {
                    logger.info("All check Passed as no validation for imei {}", request);
                    return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getName(), request.getMsisdnFilled().getCode(), ReasonCode.allCheckPassed.getCode(), protocol, SUCCESS);
                }
            } else {
                logger.info("DeviceType Global Flag is Off {}", request);
                return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getCode(), configFlagService.getImeiAllCheckPassConfigStatus().getName(), request.getMsisdnFilled().getCode(), ReasonCode.eirDeviceTypeGlobalFlagIsOff.getCode(), protocol, SUCCESS);
            }
        } else {
            coreModuleAlertService.sendImeiValidationAlert(request.getImei(), imeiValidResponse.getReasonCode());
            logger.info("Invalid IMEI Reason Code:{} {}", imeiValidResponse.getReasonCode().name(), request);
            return new VerificationResponse(request.getTid(), request.getImeiReceived(), request.getImsi(), request.getMsisdn(), request.getTac(), DeviceType.Others.getCode(), imeiValidResponse.getStatusValue().getCode(), imeiValidResponse.getStatusValue().getName(), request.getMsisdnFilled().getCode(), imeiValidResponse.getReasonCode().getCode(), protocol, SUCCESS);
        }

    }

}
