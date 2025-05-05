package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.dto.http.ResponseDto;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.orchestrator.AllListsOrchestrator;
import com.gl.ceir.flowManager.service.ITrackedListService;
import com.gl.ceir.flowManager.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ResourcesUrls.TRACKED_LIST_RESOURCE_PATH)
public class TrackedListController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ITrackedListService trackedListService;

    @Autowired
    AllListsOrchestrator listsOrchestrator;

    @Autowired
    DateUtil dateUtil;

    @GetMapping("/load-data")
    public LoadResponse loadTrackedList() {
        try {
            int size = trackedListService.loadTrackedList();
            return new LoadResponse(true, LoadApiConstants.TRACKED_LIST_SUCCESS, null, size);
        } catch (Exception e) {
            return new LoadResponse(false, LoadApiConstants.TRACKED_LIST_FAIL, e.getMessage(), 0);
        }
    }

    @PostMapping(path = "/{imei}/{imsi}/{msisdn}/{actualImei}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> add(@PathVariable("imei") String imei, @PathVariable("imsi") String imsi, @PathVariable("msisdn")
    String msisdn, @PathVariable("actualImei") String actualImei, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        TrackedListValue trackedListValue = get(imei, imsi, msisdn, actualImei, requestDate);
        ApiStatusMessage apiStatusMessage = trackedListService.addToList(trackedListValue);
        logger.info("Added to Tracked List with Request:{} Status:{} TimeTaken:{}", trackedListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{imei}/{imsi}/{msisdn}/{actualImei}/{requestDate}")
    public ResponseEntity<ApiStatusMessage> delete(@PathVariable("imei") String imei, @PathVariable("imsi") String imsi, @PathVariable("msisdn")
    String msisdn, @PathVariable("actualImei") String actualImei, @PathVariable("requestDate") String requestDate) {
        long start = System.currentTimeMillis();
        TrackedListValue trackedListValue = get(imei, imsi, msisdn, actualImei, requestDate);
        ApiStatusMessage apiStatusMessage = trackedListService.removeFromList(trackedListValue);
        logger.info("Deleted from Tracked List with Request:{} Status:{} TimeTaken:{}", trackedListValue, apiStatusMessage.name(), (System.currentTimeMillis() - start));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.ACCEPTED);
    }

    private TrackedListValue get(String imei, String imsi, String msisdn, String actualImei, String requestDate) {
        imei = LoadApiConstants.NULL.equals(imei) ? null : imei;
        imsi = LoadApiConstants.NULL.equals(imsi) ? null : imsi;
        msisdn = LoadApiConstants.NULL.equals(msisdn) ? null : msisdn;
        actualImei = LoadApiConstants.NULL.equals(actualImei) ? null : actualImei;
        return TrackedListValue.builder().imei(imei).imsi(imsi).msisdn(msisdn)
                .actualImei(actualImei)
                .requestDate(dateUtil.fromUrlFormat(requestDate))
                .created_on(dateUtil.getCurrentDate()).build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<ResponseDto> getAll() {
        return new ResponseEntity<ResponseDto>(listsOrchestrator.getTrackedList(), HttpStatus.OK);
    }


    @GetMapping("/count")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> count() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(ResponseDtoUtil.getSuccessResponseWithData(trackedListService.getCount()), HttpStatus.OK);
    }
}
