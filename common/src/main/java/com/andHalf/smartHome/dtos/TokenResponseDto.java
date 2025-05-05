package com.andHalf.smartHome.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {

    private String idToken;

    private String accessToken;

    private String refreshToken;

    private Integer expiryIn;

    private Boolean valid;

}
