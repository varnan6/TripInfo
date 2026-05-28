package com.tripinfo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tripInfoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TripInfo Analytics API")
                        .description("Ride-hailing trip management and driver analytics")
                        .version("1.0.0"));
    }
}