package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.orchestrator.AllListsOrchestrator;
import com.gl.ceir.flowManager.service.IBlackListService;
import com.gl.ceir.flowManager.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ResourcesUrls.BLOCKED_LIST_RESOURCE_PATH)
public class BlackListController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IBlackListService blackListService;

    @Autowired
    AllListsOrchestrator listsOrchestrator;

    @Autowired
    DateUtil dateUtil;

    @GetMapping("/load-data")
    public ResponseEntity<LoadResponse> loadBlackList() {
        try {
            int size = blackListService.loadBlackList();
            return new ResponseEntity<>(new LoadResponse(true, LoadApiConstants.BLACK_LIST_SUCCESS, null, size), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new LoadResponse(false, LoadApiConstants.BLACK_LIST_FAIL, e.getMessage(), 0), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(path = "/{imei}/{imsi}/{msisdn}/{actualImei}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> add(@PathVariable("imei") String imei, @PathVariable("imsi") String imsi, @PathVariable("msisdn")
    String msisdn, @PathVariable("actualImei") String actualImei, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        BlackListValue blackListValue = get(imei, imsi, msisdn, actualImei, requestDate);
        ApiStatusMessage apiStatusMessage = blackListService.addToList(blackListValue);
        logger.info("Added to Blocked list with Request:{} Status:{} TimeTaken:{}", blackListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{imei}/{imsi}/{msisdn}/{actualImei}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> delete(@PathVariable("imei") String imei, @PathVariable("imsi") String imsi, @PathVariable("msisdn")
    String msisdn, @PathVariable("actualImei") String actualImei, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        BlackListValue blackListValue = get(imei, imsi, msisdn, actualImei, requestDate);
        ApiStatusMessage apiStatusMessage = blackListService.removeFromList(blackListValue);
        logger.info("Deleted from Black List with Request:{} Status:{} TimeTaken:{}", blackListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.ACCEPTED);
    }

    private BlackListValue get(String imei, String imsi, String msisdn, String actualImei, String requestDate) {
        imei = LoadApiConstants.NULL.equals(imei) ? null : imei;
        imsi = LoadApiConstants.NULL.equals(imsi) ? null : imsi;
        msisdn = LoadApiConstants.NULL.equals(msisdn) ? null : msisdn;
        actualImei = LoadApiConstants.NULL.equals(actualImei) ? null : actualImei;
        return BlackListValue.builder().imei(imei).imsi(imsi).msisdn(msisdn)
                .actualImei(actualImei)
                .requestDate(dateUtil.fromUrlFormat(requestDate)).created_on(dateUtil.getCurrentDate()).build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> getAll() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(listsOrchestrator.getBlackList(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> count() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(ResponseDtoUtil.getSuccessResponseWithData(blackListService.getCount()), HttpStatus.OK);
    }
}
