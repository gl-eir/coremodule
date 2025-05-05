package com.gl.ceir.flowManager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationMappingResponseDto {
    private MappingType type;
    private String imsi;
    private String msisdn;
    private ResponseResult result;
}
