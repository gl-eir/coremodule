package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.DeviceType;
import com.gl.ceir.flowManager.contstants.ReasonCode;
import com.gl.ceir.flowManager.contstants.StatusValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeListResponseData {

    private DeviceType deviceType;

    private boolean isNotAllowed;

    private ReasonCode reasonCode;

    private StatusValue statusValue;
}
