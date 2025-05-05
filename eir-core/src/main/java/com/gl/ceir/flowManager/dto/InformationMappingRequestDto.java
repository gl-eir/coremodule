package com.gl.ceir.flowManager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InformationMappingRequestDto {
    private MappingType type;

    private String imsi;

    private String responseIp;

    private Integer responsePort;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformationMappingRequestDto that = (InformationMappingRequestDto) o;
        return imsi.equals(that.imsi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imsi);
    }
}
