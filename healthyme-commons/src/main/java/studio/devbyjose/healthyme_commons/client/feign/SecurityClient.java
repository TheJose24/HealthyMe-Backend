package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.fallback.SecurityClientFallback;

@FeignClient(name = "security-service",
             path = "/api",
             fallback = SecurityClientFallback.class)
public interface SecurityClient {
    
    @GetMapping("/users/{id}")
    ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable("id") Integer id);
}