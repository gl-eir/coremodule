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
public class UserAndPiResponseDto {
    private String status;

    private ErrorInfo errorInfo;

    private List<UserPiMapDto> result;
}
