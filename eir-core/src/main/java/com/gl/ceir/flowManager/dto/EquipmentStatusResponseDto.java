package com.gl.ceir.flowManager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EquipmentStatusResponseDto {

    @JsonProperty("EquipmentStatus")
    private String equipmentStatus;

    @JsonProperty("ProblemDetails")
    private ProblemDetailsDto problemDetail;
}
