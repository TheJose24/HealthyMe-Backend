package studio.devbyjose.healthyme_payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "studio.devbyjose.healthyme_commons.client.feign")
public class HealthymePaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthymePaymentApplication.class, args);
	}

}
