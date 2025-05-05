package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.UpdationFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonRequestReceiver {

    private String imei;
    private String imsi;
    private String msisdn;
    private UpdationFlag updationFlag;
}
