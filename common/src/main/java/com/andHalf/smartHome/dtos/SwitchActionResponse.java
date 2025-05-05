package com.andHalf.smartHome.dtos;

import com.andHalf.smartHome.payload.PinNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchActionResponse {

    PinNo pinNo;

    Boolean result;
    
}
