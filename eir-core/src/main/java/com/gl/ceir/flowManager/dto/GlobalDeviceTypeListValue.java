package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalDeviceTypeListValue {

    private String tac;
    private DeviceType device_type;
}
