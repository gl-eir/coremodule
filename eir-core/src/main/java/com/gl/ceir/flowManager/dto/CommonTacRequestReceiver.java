package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.UpdationFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonTacRequestReceiver {

    private String tac;
    private UpdationFlag updationFlag;


}
