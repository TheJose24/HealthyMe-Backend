package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.fallback.RecetaClientFallback;

import java.util.Map;

@FeignClient(name = "healthyme-consultas", fallback = RecetaClientFallback.class)
public interface RecetaClient {

    @GetMapping("/api/recetas/{id}")
    Map<String, Object> obtenerReceta(@PathVariable("id") Integer id);
}