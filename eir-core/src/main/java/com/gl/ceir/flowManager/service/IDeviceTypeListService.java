package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.exception.DataNotFoundException;

import java.util.Optional;

public interface IDeviceTypeListService {

    int loadDeviceTypeList();

    ApiStatusMessage addToListDB(GlobalDeviceTypeListValue value);

    ApiStatusMessage removeFromListDB(GlobalDeviceTypeListValue value);

    DeviceTypeListResponseData getDeviceTypeData(String tac);

    Long getCount();
}
