package com.gl.ceir.flowManager.alert;

import com.gl.ceir.flowManager.contstants.AlertMessagePlaceholders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {

    private String alertId;

    private String alertMessage;

    private String alertProcess;

    private String userId;

    private Map<AlertMessagePlaceholders, String> placeHolderMap;
}
