package com.andHalf.smartHome.dtos;

import com.andHalf.smartHome.payload.PinNo;
import com.andHalf.smartHome.payload.PinState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDeviceStateDto {

    private PinNo pinNo;
    private String roomName;
    private String deviceType;
    private PinState status;
    private String onTime;
    private Long approxConsumption;

}
