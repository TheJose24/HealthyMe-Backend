package dev.Elmer.healthyme_consultas.config;

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
                        .title("API - HealthyMe Consultas")
                        .version("1.0.0")
                        .description("Documentación de los endpoints para gestionar las consultas y recetas."));

    }

    @Bean
    public GroupedOpenApi consultasGroup() {
        return GroupedOpenApi.builder()
                .group("consultas")
                .packagesToScan("dev.Elmer.healthyme_consultas.controller")
                .build();
    }
}