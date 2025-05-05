package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private String imei;
    private String imsi;
    private String msisdn;
    private String created_on;
    private int status_code;
    private String msg;

}
