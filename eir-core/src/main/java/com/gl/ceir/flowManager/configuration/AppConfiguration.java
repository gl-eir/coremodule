package com.gl.ceir.flowManager.configuration;

import com.gl.ceir.flowManager.contstants.ApplicationType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AppConfiguration {

    @Value("${eir.udp.server.port}")
    private int udpServerPort;

    @Value("${spring.application.type}")
    private ApplicationType applicationType;

    @Value("${files.path}")
    private String filePath;

    @Value("${imeiNullPattern}")
    private String imeiNullPattern;

    public String getImeiNullPattern() {
        return "".equals(imeiNullPattern) ? null : imeiNullPattern;
    }

}
