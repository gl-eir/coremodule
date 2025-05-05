package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.alert.AlertConfig;
import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.ConfigFlag;
import com.gl.ceir.flowManager.contstants.DeviceType;
import com.gl.ceir.flowManager.contstants.StatusValue;
import com.gl.ceir.flowManager.entity.ConfigFlagEntity;
import com.gl.ceir.flowManager.repository.FlowFlagsRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfigFlagsService implements IConfigFlagService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private Map<ConfigFlag, String> configFlagHM = new ConcurrentHashMap<>();

    @Autowired
    AppConfiguration appConfiguration;

    @Autowired
    AlertConfig alertConfig;

    // This method will be called by spring when process comes up.
    @PostConstruct
    public void myInit() {
        loadAllConfig();
    }

    @Autowired
    FlowFlagsRepository flowFlagsRepository;

    @Override
    public int loadAllConfig() {
        List<ConfigFlagEntity> fullConfigFlag = flowFlagsRepository.findByModule(alertConfig.getProcessId());
        for (ConfigFlagEntity configFlagElement : fullConfigFlag) {
            configFlagHM.put(configFlagElement.getName(), configFlagElement.getValue());
            logger.info("Filled Config deviceType:{} value:{}", configFlagElement.getName(), configFlagElement.getValue());
        }
        logger.info("Config flag data load count : {}", configFlagHM.size());
        return configFlagHM.size();
    }

    @Override
    public boolean isForeignImsiCheckOn() {

        String value = configFlagHM.get(ConfigFlag.eir_foreign_imsi_check_enable);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }

    @Override
    public boolean isForeignImsiCheckOff() {
        return !isForeignImsiCheckOn();
    }

    @Override
    public StatusValue getDeviceTypeOffStatus(DeviceType deviceType) {
        ConfigFlag key = ConfigFlag.valueOf(deviceType.name());
        String value = configFlagHM.get(key);
        return getStatusValue(value);
    }

    @Override
    public StatusValue getTacNotFoundInGSMAStatus() {
        String value = configFlagHM.get(ConfigFlag.eir_tac_not_in_gsma_response);
        return getStatusValue(value);
    }

    public boolean isWorkFlowGlobalOff() {
        return !isWorkFlowGlobalOn();
    }

    public boolean isWorkFlowGlobalOn() {
        if (ObjectUtils.isEmpty(configFlagHM.get(ConfigFlag.eir_workflow_global)))
            return false;    //OFF means exit and send white list
        else if (configFlagHM.get(ConfigFlag.eir_workflow_global).equalsIgnoreCase("on"))
            return true;   //ON means do all the checking
        else return false; //OFF means exit and send white list
    }


    @Override
    public boolean isDeviceTypeGlobalOff() {
        return !isDeviceTypeGlobalOn();
    }

    @Override
    public boolean isDeviceTypeGlobalOn() {
        if (ObjectUtils.isEmpty(configFlagHM.get(ConfigFlag.eir_devicetype_global)) || configFlagHM.get(ConfigFlag.eir_devicetype_global).equalsIgnoreCase("off"))
            return false;
        else if (configFlagHM.get(ConfigFlag.eir_devicetype_global).equalsIgnoreCase("on")) return true;
        else return false;
    }


    @Override
    public boolean isBlackListOff() {
        return !isBlackListOn();
    }

    @Override
    public boolean isBlackListOn() {
        String value = configFlagHM.get(ConfigFlag.eir_blockedlist);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }

    @Override
    public StatusValue getNullImeiConfig() {
        String value = configFlagHM.get(ConfigFlag.eir_null_imei_response_status);
        return getStatusValue(value);
    }

    @Override
    public StatusValue getImeiLengthConfigStatus() {
        String value = configFlagHM.get(ConfigFlag.eir_imei_length_response_status);
        return getStatusValue(value);
    }

    @Override
    public StatusValue getImeiNotNumericConfigStatus() {
        String value = configFlagHM.get(ConfigFlag.eir_imei_not_numeric_response_status);
        return getStatusValue(value);
    }

    @Override
    public StatusValue getImeiAllZerosConfigStatus() {
        String value = configFlagHM.get(ConfigFlag.eir_imei_all_zeros_response_status);
        return getStatusValue(value);
    }

    @Override
    public StatusValue getImeiAllCheckPassConfigStatus() {
        String value = configFlagHM.get(ConfigFlag.eir_imei_all_check_pass_response_status);
        return getStatusValue(value);
    }

    @Override
    public boolean isImeiLengthCheckOn() {
        String value = configFlagHM.get(ConfigFlag.eir_enable_imei_length_validation);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }

    @Override
    public boolean isImeiAllZeroCheckOn() {
        String value = configFlagHM.get(ConfigFlag.eir_enable_imei_allzero_validation);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }

    @Override
    public boolean isImeiNumericCheckOn() {
        String value = configFlagHM.get(ConfigFlag.eir_enable_imei_numeric_validation);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }

    @Override
    public boolean isTrackedListOff() {
        return !isTrackedListOn();
    }

    @Override
    public boolean isTrackedListOn() {
        String value = configFlagHM.get(ConfigFlag.eir_trackedlist);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }


    @Override
    public boolean isExceptionListOff() {
        return !isExceptionListOn();
    }

    @Override
    public boolean isExceptionListOn() {
        String value = configFlagHM.get(ConfigFlag.eir_exceptionlist);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }

    @Override
    public boolean isBlockedTacOff() {
        return !isBlockedTacOn();
    }

    @Override
    public boolean isBlockedTacOn() {
        String value = configFlagHM.get(ConfigFlag.eir_blockedtaclist);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }


    @Override
    public boolean isAllowedTacOff() {
        return !isAllowedTacOn();
    }

    @Override
    public boolean isDeviceTypeOn(DeviceType deviceType) {
        ConfigFlag key = ConfigFlag.valueOf(deviceType.name());
        String value = configFlagHM.get(key);
        logger.info("From Config deviceType:{} value:{}", key, value);
        if (StringUtils.equalsIgnoreCase(value, "ON")) return true;
        else return false;
    }

    @Override
    public boolean isAllowedTacOn() {
        String value = configFlagHM.get(ConfigFlag.eir_allowedtaclist);
        if (ObjectUtils.isEmpty(value) || value.equalsIgnoreCase("off")) return false;
        else if (value.equalsIgnoreCase("on")) return true;
        else return false;
    }

    private StatusValue getStatusValue(String value) {
        if (StringUtils.startsWithAny(value, new String[]{"BLOCK", "block", "Block"})) return StatusValue.blacklist;
        else if (StringUtils.startsWithAny(value, new String[]{"UNKNOWN", "unknown", "Unknown"}))
            return StatusValue.unknown;
        else if (StringUtils.startsWithAny(value, new String[]{"GREY", "grey", "Grey"}))
            return StatusValue.greylist;
        else return StatusValue.whitelist;
    }
}
