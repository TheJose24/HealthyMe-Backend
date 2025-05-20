package dev.juliancamacho.healthyme_personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients(basePackages = {"studio.devbyjose.healthyme_commons.client.feign"})
public class HealthymePersonalApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthymePersonalApplication.class, args);
	}

}
