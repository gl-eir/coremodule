package com.gl.ceir.flowManager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetResponseDto {

    private List<GetDto> list = new ArrayList<>();

    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public static class GetDto {
        private String imei;
        private String imsie;
        private String msisdn;
        private String tac;
    }

}
