package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionListValue {

    private String imei;
    private String imsi;
    private String msisdn;
    private LocalDateTime created_on;
    private String actualImei;
    private LocalDateTime requestDate;
}
