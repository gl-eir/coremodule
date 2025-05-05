package com.andHalf.smartHome.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchActionResponseDto {
    private String status;

    private ErrorInfo errorInfo;

    private SwitchActionResponse result;
}
