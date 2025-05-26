package dev.diegoqm.healthyme_citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"studio.devbyjose.healthyme_commons.client.feign"})
public class HealthymeCitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthymeCitasApplication.class, args);
    }
}
