package studio.devbyjose.healthyme_notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "studio.devbyjose.healthyme_commons.client.feign")
@EnableKafka
public class HealthymeNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthymeNotificationApplication.class, args);
	}

}
