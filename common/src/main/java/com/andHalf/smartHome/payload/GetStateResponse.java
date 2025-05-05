package com.andHalf.smartHome.payload;

import com.andHalf.smartHome.dtos.HomeDeviceStateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStateResponse {

    List<PinStateResponse> result;

}
