package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.ReasonCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResponseData {

    private String created_on;

    private ReasonCode reasonCode;

    private boolean present;

    public ListResponseData(String created_on, ReasonCode reasonCode) {
        this.created_on = created_on;
        this.reasonCode = reasonCode;
    }
}
