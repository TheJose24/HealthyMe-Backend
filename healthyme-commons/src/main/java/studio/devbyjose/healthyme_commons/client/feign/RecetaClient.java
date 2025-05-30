package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.dto.RecetaDTO;
import studio.devbyjose.healthyme_commons.client.fallback.RecetaClientFallback;

import java.util.Map;

@FeignClient(name = "healthyme-consultas", path = "/api/v1/recetas", fallback = RecetaClientFallback.class)
public interface RecetaClient {

    @GetMapping("/{id}")
    ResponseEntity<RecetaDTO> obtenerReceta(@PathVariable("id") Integer id);

    @GetMapping("/{id}/pdf")
    ResponseEntity<byte[]> obtenerPdfReceta(@PathVariable("id") Integer id);
}