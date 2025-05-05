package com.gl.ceir.flowManager.orchestrator;

import com.gl.ceir.flowManager.contstants.DeviceSyncRequestListIdentity;
import com.gl.ceir.flowManager.dto.http.ResponseDto;
import com.gl.ceir.flowManager.dto.http.ResponseDtoUtil;
import com.gl.ceir.flowManager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class AllListsOrchestrator {

    @Autowired
    private IExceptionListService exceptionListService;

    @Autowired
    private IBlockedTACListService blockedTACListService;

    @Autowired
    private IBlackListService blackListService;

    @Autowired
    private ITrackedListService trackedListService;

    @Autowired
    private IAllowedTACListService allowedTACListService;


    @Autowired
    private IHlrDumpService hlrDumpService;

    @Autowired
    private IDeviceTypeListService deviceTypeListService;

    public ResponseDto getAllowedTac() {
        return ResponseDtoUtil.getSuccessResponseWithData(allowedTACListService.get());
    }


    public ResponseDto getBlockedTac() {
        return ResponseDtoUtil.getSuccessResponseWithData(blockedTACListService.get());
    }


    public ResponseDto getBlackList() {
        return ResponseDtoUtil.getSuccessResponseWithData(blackListService.get());
    }


    public ResponseDto getTrackedList() {
        return ResponseDtoUtil.getSuccessResponseWithData(trackedListService.get());
    }


    public ResponseDto getExceptionList() {
        return ResponseDtoUtil.getSuccessResponseWithData(exceptionListService.get());
    }

    public ResponseDto getAllListCount() {
        HashMap<DeviceSyncRequestListIdentity, Long> countMap = new HashMap<>();
        countMap.put(DeviceSyncRequestListIdentity.BLOCKED_LIST, blackListService.getCount());
        countMap.put(DeviceSyncRequestListIdentity.EXCEPTION_LIST, exceptionListService.getCount());
        countMap.put(DeviceSyncRequestListIdentity.BLOCKED_TAC, blockedTACListService.getCount());
        countMap.put(DeviceSyncRequestListIdentity.TRACKED_LIST, trackedListService.getCount());
        countMap.put(DeviceSyncRequestListIdentity.ALLOWED_TAC, allowedTACListService.getCount());
        countMap.put(DeviceSyncRequestListIdentity.HLR_DATA, hlrDumpService.getCount());
        countMap.put(DeviceSyncRequestListIdentity.GSMA_DATA, deviceTypeListService.getCount());
        return ResponseDtoUtil.getSuccessResponseWithData(countMap);
    }
}
