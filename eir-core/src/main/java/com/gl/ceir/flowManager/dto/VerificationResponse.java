package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {

    private String tid;
    private String imei;
    private String imsi;
    private String msisdn;
    private String tac;
    private Integer deviceType;
    private Integer status;
    private String value;
    private Integer appliedListName;
    private Integer reasonCode;
    private String protocol;
    private String result;

}
