package studio.devbyjose.healthyme_reclamaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "studio.devbyjose.healthyme_commons.client.feign")
public class HealthymeReclamacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthymeReclamacionesApplication.class, args);
	}

}
