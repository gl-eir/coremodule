package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.orchestrator.AllListsOrchestrator;
import com.gl.ceir.flowManager.service.IExceptionListService;
import com.gl.ceir.flowManager.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ResourcesUrls.EXCEPTION_LIST_RESOURCE_PATH)
public class ExceptionListController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IExceptionListService exceptionListService;

    @Autowired
    AllListsOrchestrator listsOrchestrator;

    @Autowired
    DateUtil dateUtil;

    @GetMapping("/load-data")
    public LoadResponse loadExceptionList() {
        try {
            int size = exceptionListService.loadExceptionList();
            return new LoadResponse(true, LoadApiConstants.EXCEPTION_LIST_SUCCESS, null, size);
        } catch (Exception e) {
            return new LoadResponse(false, LoadApiConstants.EXCEPTION_LIST_FAIL, e.getMessage(), 0);
        }
    }

    @PostMapping(path = "/{imei}/{imsi}/{msisdn}/{actualImei}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> add(@PathVariable("imei") String imei, @PathVariable("imsi") String imsi, @PathVariable("msisdn") String msisdn
            , @PathVariable("actualImei") String actualImei, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        ExceptionListValue exceptionListValue = get(imei, imsi, msisdn, actualImei, requestDate);
        ApiStatusMessage apiStatusMessage = exceptionListService.addToList(exceptionListValue);
        logger.info("Added to Exception List with Request:{} Status:{} TimeTaken:{}", exceptionListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{imei}/{imsi}/{msisdn}/{actualImei}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> delete(@PathVariable("imei") String imei, @PathVariable("imsi") String imsi, @PathVariable("msisdn") String msisdn, @PathVariable("actualImei") String actualImei, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        ExceptionListValue exceptionListValue = get(imei, imsi, msisdn, actualImei, requestDate);
        ApiStatusMessage apiStatusMessage = exceptionListService.removeFromList(exceptionListValue);
        logger.info("Deleted from Exception List with Request:{} Status:{} TimeTaken:{}", exceptionListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.ACCEPTED);
    }

    private ExceptionListValue get(String imei, String imsi, String msisdn, String actualImei, String requestDate) {
        imei = LoadApiConstants.NULL.equals(imei) ? null : imei;
        imsi = LoadApiConstants.NULL.equals(imsi) ? null : imsi;
        msisdn = LoadApiConstants.NULL.equals(msisdn) ? null : msisdn;
        actualImei = LoadApiConstants.NULL.equals(actualImei) ? null : actualImei;
        return ExceptionListValue.builder().imei(imei).imsi(imsi).msisdn(msisdn)
                .actualImei(actualImei)
                .requestDate(dateUtil.fromUrlFormat(requestDate)).created_on(dateUtil.getCurrentDate()).build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> getAll() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(listsOrchestrator.getExceptionList(), HttpStatus.OK);
    }


    @GetMapping("/count")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> count() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(ResponseDtoUtil.getSuccessResponseWithData(exceptionListService.getCount()), HttpStatus.OK);
    }
}
