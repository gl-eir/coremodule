package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackListRequestDto {

    private String imei;
    private String imsi;
    private String msisdn;
    private String updationFlag;
}
