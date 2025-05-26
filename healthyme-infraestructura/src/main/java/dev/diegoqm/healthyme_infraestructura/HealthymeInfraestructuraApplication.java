package dev.diegoqm.healthyme_infraestructura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
public class HealthymeInfraestructuraApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthymeInfraestructuraApplication.class, args);
    }
}
