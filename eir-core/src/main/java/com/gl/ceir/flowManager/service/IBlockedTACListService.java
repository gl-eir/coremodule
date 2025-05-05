package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import org.springframework.cglib.core.Block;

import java.util.List;
import java.util.Optional;

public interface IBlockedTACListService {

    /**
     * This method will load all tha data from the table to the in memory cache
     */
    int loadBlockedTACList();

    ApiStatusMessage removeFromList(BlockedTACListValue value);


    ApiStatusMessage addToList(BlockedTACListValue value);

    ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException;

    List<BlockedTacKey> get();

    Long getCount();
}
