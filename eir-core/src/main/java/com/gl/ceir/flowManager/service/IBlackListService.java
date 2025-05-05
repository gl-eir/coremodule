package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.exception.DataNotFoundException;

import java.util.List;

public interface IBlackListService {
    /**
     * This method will load all tha data from the table to the in memory cache
     */
    int loadBlackList();

    ApiStatusMessage removeFromList(BlackListValue values);


    ApiStatusMessage addToList(BlackListValue value);

    ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException;

    List<BlackListKey> get();
    Long getCount();

}
