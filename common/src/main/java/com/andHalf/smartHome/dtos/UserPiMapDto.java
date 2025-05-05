package com.andHalf.smartHome.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPiMapDto {

    private String userId;

    private String piId;

    private String status;

    private String connectStatus;

    private String piLabel;
}
