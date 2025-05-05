package com.andHalf.smartHome.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDeviceStateResponseDto {
    private String status;

    private ErrorInfo errorInfo;

    private HomeDeviceStateResponse result;
}
