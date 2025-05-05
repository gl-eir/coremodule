package com.gl.ceir.flowManager.configuration;

import com.gl.ceir.flowManager.dto.VerificationRequest;
import com.gl.ceir.flowManager.dto.VerificationResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@Data
public class InfoMapperUdpServerConfig {

    @Value("${info-mapper.enable}")
    private Boolean infoMapperIsEnabled;

}
