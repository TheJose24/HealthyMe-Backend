package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_commons.client.dto.CitaDTO;
import studio.devbyjose.healthyme_commons.client.fallback.CitaClientFallback;

import java.util.List;

@FeignClient(name = "healthyme-citas", fallback = CitaClientFallback.class)
public interface CitaClient {

    @PostMapping("/api/v1/citas")
    CitaDTO createCita(@RequestBody CitaDTO citaDTO);

    @GetMapping("/api/v1/citas/{id}")
    CitaDTO getCitaById(@PathVariable("id") Integer id);

    @GetMapping("/api/v1/citas")
    List<CitaDTO> getAllCitas();

    @PutMapping("/api/v1/citas/{id}")
    CitaDTO updateCita(@PathVariable("id") Integer id,
                       @RequestBody CitaDTO citaDTO);

    @DeleteMapping("/api/v1/citas/{id}")
    void deleteCitaById(@PathVariable("id") Integer id);
}
