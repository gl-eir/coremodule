package com.andHalf.smartHome.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PiDto {

    private String piId;

    private String macAddress;

    private String serialNo;

    private String status;
}
