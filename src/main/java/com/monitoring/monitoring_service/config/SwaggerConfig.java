package com.monitoring.monitoring_service.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on Aug, 2025
 *
 * @author s Bostan
 */
public class SwaggerConfig {
    @Bean
    public OpenAPI monitoringServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Monitoring Service API")
                        .description("API documentation for Monitoring Microservice")
                        .version("1.0.0"));
    }
}
