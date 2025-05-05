package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExemptDeviceTypeApiResponse {

    private String name;
    private String imei_exempt;
    private String flag;
    private int status_code;
    private String msg;

}
