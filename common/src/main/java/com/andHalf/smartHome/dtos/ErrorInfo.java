package com.andHalf.smartHome.dtos;

import com.andHalf.smartHome.constants.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo {
    private int errorCode;

    private ErrorEnum errorLevel;

    private String errorMessage;
}
