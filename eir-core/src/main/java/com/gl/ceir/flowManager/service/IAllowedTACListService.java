package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.exception.DataNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IAllowedTACListService {

    int loadAllowedTACList();

    ApiStatusMessage removeFromList(AllowedTACListValue value);


    ApiStatusMessage addToList(AllowedTACListValue value);

    ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException;

    List<AllowedTacKey> get();

    Long getCount();
}
