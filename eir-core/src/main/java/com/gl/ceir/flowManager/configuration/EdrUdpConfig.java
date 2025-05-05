package com.gl.ceir.flowManager.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class EdrUdpConfig {

    @Value("${edr.server.ip}")
    private String edrServerIp;

    @Value("${edr.server.port}")
    private Integer edrServerPort;

}
