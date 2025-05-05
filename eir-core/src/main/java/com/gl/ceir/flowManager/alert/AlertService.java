package com.gl.ceir.flowManager.alert;

import com.gl.ceir.flowManager.contstants.AlertIds;
import com.gl.ceir.flowManager.contstants.AlertMessagePlaceholders;

import java.util.Map;

public interface AlertService {

    void sendAlert(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap);
}
