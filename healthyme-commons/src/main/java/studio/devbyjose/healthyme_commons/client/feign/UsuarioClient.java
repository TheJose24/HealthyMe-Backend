package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.fallback.UsuarioClientFallback;

@FeignClient(name = "security-service", fallback = UsuarioClientFallback.class)
public interface UsuarioClient {

    @GetMapping("api/users/{id}")
    UsuarioDTO obtenerUsuario(@PathVariable("id") Integer id);
}
