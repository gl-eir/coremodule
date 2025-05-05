package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

    private String imei;
    private String imsi;
    private String msisdn;
    private String created_on;
    private String msg;
    private int status_code;

}
