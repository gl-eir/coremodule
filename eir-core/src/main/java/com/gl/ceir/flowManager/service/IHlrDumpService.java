package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.dto.ExceptionListValue;
import com.gl.ceir.flowManager.exception.DataNotFoundException;

public interface IHlrDumpService {

    int loadHlrDump();

    String getMsisdn(String imsi) throws DataNotFoundException;

    ApiStatusMessage removeFromList(String imsie, String msisdn);

    ApiStatusMessage addToList(String imsie, String msisdn);

    Long getCount();
}
