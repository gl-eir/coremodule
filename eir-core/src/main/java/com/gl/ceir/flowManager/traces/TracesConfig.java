package com.gl.ceir.flowManager.traces;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Data
@ConfigurationProperties(prefix = "trace-request")
public class TracesConfig {

    private String ip;

    private Integer port;

    private Boolean traceBlackFlag;

    private Boolean traceGreyFlag;

}
