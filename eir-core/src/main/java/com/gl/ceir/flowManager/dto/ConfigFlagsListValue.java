package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.ConfigFlag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigFlagsListValue {

    @NotNull
    private ConfigFlag name;

    @NotNull
    private String value;


}
