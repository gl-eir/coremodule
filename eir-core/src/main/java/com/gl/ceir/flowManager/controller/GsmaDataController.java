package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.DeviceType;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.GlobalDeviceTypeListValue;
import com.gl.ceir.flowManager.dto.LoadResponse;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.service.IDeviceTypeListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ResourcesUrls.GSMA_DATA_RESOURCE_PATH)
public class GsmaDataController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IDeviceTypeListService deviceTypeListService;

    @GetMapping("/load-data")
    public LoadResponse loadExceptionList() {
        try {
            int size = deviceTypeListService.loadDeviceTypeList();
            return new LoadResponse(true, LoadApiConstants.GSMA_DATA_SUCCESS, null, size);
        } catch (Exception e) {
            return new LoadResponse(false, LoadApiConstants.GSMA_DATA_FAIL, e.getMessage(), 0);
        }
    }

    @PostMapping(path = "/{tac}")
    public ResponseEntity<ApiStatusMessage> add(@PathVariable("tac") String tac, @RequestParam("deviceType") String deviceType) {
        ApiStatusMessage apiStatusMessage = deviceTypeListService.addToListDB(new GlobalDeviceTypeListValue(tac, DeviceType.findByName(deviceType)));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{tac}")
    public ResponseEntity<ApiStatusMessage> delete(@PathVariable("tac") String tac, @RequestParam("deviceType") String deviceType) {
        ApiStatusMessage apiStatusMessage = deviceTypeListService.removeFromListDB(new GlobalDeviceTypeListValue(tac, DeviceType.findByName(deviceType)));
        return new ResponseEntity<ApiStatusMessage>(apiStatusMessage, HttpStatus.ACCEPTED);
    }

    @GetMapping("/count")
    public ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto> count() {
        return new ResponseEntity<com.gl.ceir.flowManager.dto.http.ResponseDto>(ResponseDtoUtil.getSuccessResponseWithData(deviceTypeListService.getCount()), HttpStatus.OK);
    }
}
