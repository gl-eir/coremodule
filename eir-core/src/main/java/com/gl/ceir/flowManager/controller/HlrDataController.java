package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.LoadResponse;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.service.IHlrDumpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ResourcesUrls.HLR_DATA_RESOURCE_PATH)
public class HlrDataController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IHlrDumpService hlrDumpService;

    @GetMapping("/load-data")
    public LoadResponse loadExceptionList() {
        try {
            int size = hlrDumpService.loadHlrDump();
            return new LoadResponse(true, LoadApiConstants.HLR_DUMP_SUCCESS, null, size);
        } catch (Exception e) {
            return new LoadResponse(false, LoadApiConstants.HLR_DUMP_FAIL, e.getMessage(), 0);
        }
    }

    @PostMapping(path = "/{imsi}/{msisdn}")
    public ResponseEntity<ApiStatusMessage> add(@PathVariable("imsi") String imsi, @PathVariable("msisdn") String msisdn) {
        ApiStatusMessage apiStatusMessage = hlrDumpService.addToList(imsi, msisdn);
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{imsi}/{msisdn}")
    public ResponseEntity<ApiStatusMessage> delete(@PathVariable("imsi") String imsi, @PathVariable("msisdn") String msisdn) {
        ApiStatusMessage apiStatusMessage = hlrDumpService.removeFromList(imsi, msisdn);
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.ACCEPTED);
    }

    @GetMapping("/count")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> count() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(ResponseDtoUtil.getSuccessResponseWithData(hlrDumpService.getCount()), HttpStatus.OK);
    }
}
