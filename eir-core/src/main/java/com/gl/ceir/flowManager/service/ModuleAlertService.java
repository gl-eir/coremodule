package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.alert.AlertService;
import com.gl.ceir.flowManager.contstants.AlertIds;
import com.gl.ceir.flowManager.contstants.AlertMessagePlaceholders;
import com.gl.ceir.flowManager.contstants.ReasonCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModuleAlertService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlertService alertService;

    public void sendImeiNullAlert(String tid) {
        Map<AlertMessagePlaceholders, String> map = new HashMap<>();
        map.put(AlertMessagePlaceholders.TID, tid);
        alertService.sendAlert(AlertIds.IMEI_MISSING_IN_REQUEST, map);
    }

    public void sendImsiNullAlert(String tid) {
        Map<AlertMessagePlaceholders, String> map = new HashMap<>();
        map.put(AlertMessagePlaceholders.TID, tid);
        alertService.sendAlert(AlertIds.IMSI_MISSING_IN_REQUEST, map);
    }

    public void sendUdpQueueSizeAlert(Integer size) {
        if (size > 1000) {
            Map<AlertMessagePlaceholders, String> map = new HashMap<>();
            map.put(AlertMessagePlaceholders.QUEUE_SIZE, String.valueOf(size));
            alertService.sendAlert(AlertIds.UDP_QUEUE_SIZE_INCREASED, map);
        }
    }

    public void sendImeiValidationAlert(String imei, ReasonCode reasonCode) {
        Map<AlertMessagePlaceholders, String> map = new HashMap<>();
        map.put(AlertMessagePlaceholders.REASON_CODE, reasonCode.name());
        map.put(AlertMessagePlaceholders.IMEI, imei);
        alertService.sendAlert(AlertIds.IMEI_VALIDATIONS_FAILED, map);
    }
}
