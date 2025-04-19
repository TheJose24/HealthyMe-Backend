package studio.devbyjose.healthyme_storage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class StorageConfig implements WebMvcConfigurer {

    @Value("${storage.location}")
    private String storageLocation;

    @Value("${storage.max-file-size:10MB}")
    private DataSize maxFileSize;

    @Value("${storage.max-capacity-mb:1024}")
    private long maxCapacityMb;

    /**
     * Configura el directorio de almacenamiento al iniciar la aplicación
     */
    @Bean
    CommandLineRunner initStorage() {
        return args -> {
            try {
                Path rootLocation = Paths.get(storageLocation);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                    log.info("Directorio de almacenamiento creado: {}", rootLocation);
                } else {
                    log.info("Directorio de almacenamiento encontrado: {}", rootLocation);
                }

                log.info("Configuración de almacenamiento:");
                log.info("- Ubicación: {}", storageLocation);
                log.info("- Tamaño máximo por archivo: {}", maxFileSize);
                log.info("- Capacidad máxima: {}MB", maxCapacityMb);

            } catch (IOException e) {
                log.error("Error al inicializar el directorio de almacenamiento", e);
                throw new RuntimeException("Error al inicializar el directorio de almacenamiento", e);
            }
        };
    }

}