package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacApiResponse {

    private String tac;
    private String created_on;
    private int status_code;
    private String msg;

}
