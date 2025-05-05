package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.ConfigFlag;
import com.gl.ceir.flowManager.contstants.DeviceType;
import com.gl.ceir.flowManager.contstants.StatusValue;
import com.gl.ceir.flowManager.dto.AllowedTACListValue;
import com.gl.ceir.flowManager.dto.ConfigFlagsListValue;
import com.gl.ceir.flowManager.dto.CountApiResponse;

import java.util.Optional;

public interface IConfigFlagService {

    int loadAllConfig();

    boolean isWorkFlowGlobalOn();

    boolean isWorkFlowGlobalOff();

    boolean isDeviceTypeGlobalOn();

    boolean isDeviceTypeGlobalOff();

    boolean isBlackListOn();

    StatusValue getNullImeiConfig();

    StatusValue getImeiLengthConfigStatus();

    StatusValue getImeiNotNumericConfigStatus();

    StatusValue getImeiAllZerosConfigStatus();
    StatusValue getImeiAllCheckPassConfigStatus();

    boolean isImeiLengthCheckOn();

    boolean isImeiAllZeroCheckOn();

    boolean isImeiNumericCheckOn();

    boolean isBlackListOff();

    boolean isTrackedListOn();

    boolean isTrackedListOff();

    boolean isExceptionListOn();

    boolean isExceptionListOff();

    boolean isBlockedTacOn();

    boolean isBlockedTacOff();

    boolean isAllowedTacOn();

    boolean isAllowedTacOff();

    boolean isDeviceTypeOn(DeviceType deviceType);

    boolean isForeignImsiCheckOn();

    boolean isForeignImsiCheckOff();

    StatusValue getDeviceTypeOffStatus(DeviceType deviceType);

    StatusValue getTacNotFoundInGSMAStatus();
}
