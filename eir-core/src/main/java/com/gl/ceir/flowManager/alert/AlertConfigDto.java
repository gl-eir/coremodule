package com.gl.ceir.flowManager.alert;

import lombok.Data;

@Data
public class AlertConfigDto {
    String alertId;
    String message;
    Boolean enable;
}
