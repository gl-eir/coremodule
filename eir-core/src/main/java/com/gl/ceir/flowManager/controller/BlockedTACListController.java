package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.dto.http.ResponseDto;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.orchestrator.AllListsOrchestrator;
import com.gl.ceir.flowManager.service.IBlockedTACListService;
import com.gl.ceir.flowManager.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = ResourcesUrls.BLOCKED_TAC_RESOURCE_PATH)
public class BlockedTACListController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IBlockedTACListService blockedTACListService;

    @Autowired
    AllListsOrchestrator listsOrchestrator;

    @Autowired
    DateUtil dateUtil;

    @GetMapping("/load-data")
    public LoadResponse loadBlockedTACList() {
        try {
            int size = blockedTACListService.loadBlockedTACList();
            return new LoadResponse(true, LoadApiConstants.BLOCKED_TAC_LIST_SUCCESS, null, size);
        } catch (Exception e) {
            return new LoadResponse(false, LoadApiConstants.BLOCKED_TAC_LIST_FAIL, e.getMessage(), 0);
        }
    }

    @PostMapping(path = "/{tac}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> add(@PathVariable("tac") String tac, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        BlockedTACListValue blockedTacListValue = new BlockedTACListValue();
        blockedTacListValue.setTac(tac);
        blockedTacListValue.setCreated_on(dateUtil.getCurrentDate());
        blockedTacListValue.setRequestDate(dateUtil.fromUrlFormat(requestDate));
        ApiStatusMessage apiStatusMessage = blockedTACListService.addToList(blockedTacListValue);
        logger.info("Added to Blocked Tac with Request:{} Status:{} TimeTaken:{}", blockedTacListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{tac}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> delete(@PathVariable("tac") String tac, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        BlockedTACListValue blockedTacListValue = new BlockedTACListValue();
        blockedTacListValue.setTac(tac);
        blockedTacListValue.setCreated_on(dateUtil.getCurrentDate());
        blockedTacListValue.setRequestDate(dateUtil.fromUrlFormat(requestDate));
        ApiStatusMessage apiStatusMessage = blockedTACListService.removeFromList(blockedTacListValue);
        logger.info("Deleted from Blocked Tac with Request:{} Status:{} TimeTaken:{}", blockedTacListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-all")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> getAll() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(listsOrchestrator.getBlockedTac(), HttpStatus.OK);
    }


    @GetMapping("/count")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> count() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(ResponseDtoUtil.getSuccessResponseWithData(blockedTACListService.getCount()), HttpStatus.OK);
    }
}
