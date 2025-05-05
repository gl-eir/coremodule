package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountApiResponse {

    private int ImeiImsiMsisdn;
    private int ImeiImsi;
    private int ImeiMsisdn;
    private int ImsiMsisdn;
    private int Imei;
    private int Imsi;
    private int msisdn;
    private long totalCacheCount;
    private long totalDbCount;

}
