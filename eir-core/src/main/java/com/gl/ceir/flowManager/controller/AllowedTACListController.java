package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.orchestrator.AllListsOrchestrator;
import com.gl.ceir.flowManager.service.IAllowedTACListService;
import com.gl.ceir.flowManager.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ResourcesUrls.ALLOWED_TAC_RESOURCE_PATH)
public class AllowedTACListController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IAllowedTACListService allowedTACListService;

    @Autowired
    AllListsOrchestrator listsOrchestrator;

    @Autowired
    DateUtil dateUtil;

    @GetMapping("/load-data")
    public LoadResponse loadAllowedTACList() {
        try {
            int size = allowedTACListService.loadAllowedTACList();
            return new LoadResponse(true, LoadApiConstants.ALLOWED_TAC_LIST_SUCCESS, null, size);
        } catch (Exception e) {
            return new LoadResponse(false, LoadApiConstants.ALLOWED_TAC_LIST_FAIL, e.getMessage(), 0);
        }
    }


    @PostMapping(path = "/{tac}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> add(@PathVariable("tac") String tac, @PathVariable("requestDate") String requestDate) {
        AllowedTACListValue allowedTacListValue = new AllowedTACListValue();
        allowedTacListValue.setCreated_on(dateUtil.getCurrentDate());
        allowedTacListValue.setRequestDate(dateUtil.fromUrlFormat(requestDate));
        allowedTacListValue.setTac(tac);
        long start = System.currentTimeMillis();
        ApiStatusMessage apiStatusMessage = allowedTACListService.addToList(allowedTacListValue);
        logger.info("Added to Allowed Tac with Request:{} Status:{} TimeTaken:{}", allowedTacListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{tac}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> delete(@PathVariable("tac") String tac, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        AllowedTACListValue allowedTacListValue = new AllowedTACListValue();
        allowedTacListValue.setTac(tac);
        allowedTacListValue.setRequestDate(dateUtil.fromUrlFormat(requestDate));
        ApiStatusMessage apiStatusMessage = allowedTACListService.removeFromList(allowedTacListValue);
        logger.info("Deleted from Allowed Tac with Request:{} Status:{} TimeTaken:{}", allowedTacListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-all")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> getAll() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(listsOrchestrator.getAllowedTac(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> count() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(ResponseDtoUtil.getSuccessResponseWithData(allowedTACListService.getCount()), HttpStatus.OK);
    }
}
