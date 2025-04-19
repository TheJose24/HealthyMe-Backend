package studio.devbyjose.healthyme_storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HealthymeStorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthymeStorageApplication.class, args);
	}

}
