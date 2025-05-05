package com.gl.ceir.flowManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadResponse {

    private boolean status;
    private String message;
    private String errorMessage;
    private int loadCount;
    private Object data;

    public LoadResponse(boolean status, String message, String errorMessage, int loadCount) {
        this.status = status;
        this.message = message;
        this.errorMessage = errorMessage;
        this.loadCount = loadCount;
    }
}
