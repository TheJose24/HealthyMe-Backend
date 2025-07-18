package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.dto.MedicoDTO;
import studio.devbyjose.healthyme_commons.client.fallback.MedicoClientFallback;

@FeignClient(name = "healthyme-personal", fallback = MedicoClientFallback.class)
public interface MedicoClient {

    @GetMapping("/api/v1/medicos/{id}")
    MedicoDTO obtenerMedico(@PathVariable("id") Integer id);
}