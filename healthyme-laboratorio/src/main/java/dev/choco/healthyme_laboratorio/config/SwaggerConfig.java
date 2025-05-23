package dev.choco.healthyme_laboratorio.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - HealthyMe Laboratorio")
                        .version("1.0.0")
                        .description("Documentación de los endpoints para gestionar las reservas de laboratorio y examenes."));

    }

    @Bean
    public GroupedOpenApi laboratorioGroup() {
        return GroupedOpenApi.builder()
                .group("laboratorio")
                .packagesToScan("dev.choco.healthyme_laboratorio.controller")
                .build();
    }
}
