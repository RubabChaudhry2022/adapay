package com.example.Project.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AuthServiceConfig {

    @Value("${auth-service.connection-timeout}")
    private int connectionTimeout;

    @Value("${auth-service.read-timeout}")
    private int readTimeout;

    @Bean(name = "authServiceRestTemplate")
    public RestTemplate authServiceRestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}
