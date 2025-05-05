package com.gl.ceir.flowManager.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class BeanConfiguration {

    @Bean(name = "receivingQueue")
    BlockingQueue<String> receivingQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Acquisition Service")
                        .description("Acquisition Service")
                        .version("1.0"));
    }

}
