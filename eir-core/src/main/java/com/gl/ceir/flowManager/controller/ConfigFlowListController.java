package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.ConfigFlag;
import com.gl.ceir.flowManager.contstants.LoadApiConstants;
import com.gl.ceir.flowManager.contstants.ResourcesUrls;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.service.IConfigFlagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = ResourcesUrls.CONFIG_PATH)
public class ConfigFlowListController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IConfigFlagService configFlagListService;

    @GetMapping("/reload")
    public LoadResponse loadConfigFlagList() {
        try {
            int size = configFlagListService.loadAllConfig();
            return new LoadResponse(true, LoadApiConstants.CONFIG_FLAG_LIST_SUCCESS, null, size, null);
        } catch (Exception e) {
            return new LoadResponse(false, LoadApiConstants.CONFIG_FLAG_LIST_FAIL, e.getMessage(), 0, null);
        }
    }

    @GetMapping("/config-keys")
    public LoadResponse getKeys() {
        return new LoadResponse(true, LoadApiConstants.CONFIG_FLAG_LIST_SUCCESS, null, ConfigFlag.values().length, ConfigFlag.values());
    }

}
