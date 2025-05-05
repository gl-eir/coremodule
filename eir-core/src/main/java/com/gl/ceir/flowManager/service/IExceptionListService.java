package com.gl.ceir.flowManager.service;


import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.exception.DataNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IExceptionListService {

    int loadExceptionList();

    ApiStatusMessage removeFromList(ExceptionListValue values);

    ApiStatusMessage addToList(ExceptionListValue value);

    ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException;

    List<ExceptionListKey> get();

    Long getCount();
}
