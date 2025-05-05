package com.andHalf.smartHome.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGetTokenDto {

    private String accessCode;

    private String redirectUri;
}
