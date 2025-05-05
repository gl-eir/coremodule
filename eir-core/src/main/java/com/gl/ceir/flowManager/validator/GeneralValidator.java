package com.gl.ceir.flowManager.validator;

import com.gl.ceir.flowManager.contstants.AppliedListName;
import com.gl.ceir.flowManager.contstants.DeviceType;
import com.gl.ceir.flowManager.contstants.ReasonCode;
import com.gl.ceir.flowManager.contstants.StatusValue;
import com.gl.ceir.flowManager.dto.BlackListValue;
import com.gl.ceir.flowManager.dto.VerificationRequest;
import com.gl.ceir.flowManager.dto.VerificationResponse;
import com.gl.ceir.flowManager.server.IUdpSender;
import com.gl.ceir.flowManager.service.IConfigFlagService;
import com.gl.ceir.flowManager.service.ModuleAlertService;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralValidator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IConfigFlagService configFlagService;

    public ValidateResponse validateImei(String imei) {
        ValidateResponse response = new ValidateResponse();
        response.setIsValid(Boolean.TRUE);
        response.setIsValidationEnabled(false);
        if (StringUtils.isBlank(imei)) {
            response.setIsValid(Boolean.FALSE);
            response.setReasonCode(ReasonCode.imeiNull);
            response.setStatusValue(configFlagService.getNullImeiConfig());
            logger.error("This imei : {} is not valid response:{}", imei, response);
        } else {
            if (configFlagService.isImeiLengthCheckOn())
                validateImeiLength(imei, response);
            if (response.isValid && configFlagService.isImeiAllZeroCheckOn())
                validateImeiAllZero(imei, response);
            if (response.isValid && configFlagService.isImeiNumericCheckOn())
                validateImeiNumeric(imei, response);
        }
        return response;
    }

    private void validateImeiLength(String imei, ValidateResponse response) {
        response.setIsValidationEnabled(true);
        // Not checking for length greater than 14 because we are formatting the IMEI when request processing starts
        if (imei.length() < 14) {
            logger.error("This imei : {} is not valid", imei);
            response.setIsValid(Boolean.FALSE);
            response.setReasonCode(ReasonCode.imeiLength);
            response.setStatusValue(configFlagService.getImeiLengthConfigStatus());
        }
    }

    private void validateImeiAllZero(String imei, ValidateResponse response) {
        response.setIsValidationEnabled(true);
        if (imei.equals("00000000000000")) {
            logger.error("This imei : {} is not valid, All Zero", imei);
            response.setIsValid(Boolean.FALSE);
            response.setReasonCode(ReasonCode.imeiAllZero);
            response.setStatusValue(configFlagService.getImeiAllZerosConfigStatus());
        }
    }


    private void validateImeiNumeric(String imei, ValidateResponse response) {
        response.setIsValidationEnabled(true);
        try {
            Double.parseDouble(imei);
        } catch (Exception e) {
            logger.error("This imei : {} is not numeric ", imei);
            response.setIsValid(Boolean.FALSE);
            response.setReasonCode(ReasonCode.imeiNotNumeric);
            response.setStatusValue(configFlagService.getImeiNotNumericConfigStatus());
        }
    }

    @Data
    public static class ValidateResponse {
        ReasonCode reasonCode;
        Boolean isValid;
        StatusValue statusValue;
        Boolean isValidationEnabled;
    }

}
