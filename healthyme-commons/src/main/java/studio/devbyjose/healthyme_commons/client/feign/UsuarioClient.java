package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.fallback.UsuarioClientFallback;

import java.util.List;

@FeignClient(name = "security-service", fallback = UsuarioClientFallback.class)
public interface UsuarioClient {

    @GetMapping("/api/v1/users/{id}")
    ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable("id") Integer id);

    @GetMapping("/api/v1/users/dni/{dni}")
    ResponseEntity<UsuarioDTO> obtenerUsuarioPorDni(@PathVariable("dni") String dni);

    @GetMapping("/api/v1/users/username/{username}")
    ResponseEntity<UsuarioDTO> obtenerUsuarioPorUsername(@PathVariable("username") String username);

    @GetMapping("/api/v1/users/by-role/{rolNombre}")
    ResponseEntity<List<UsuarioDTO>> obtenerUsuariosPorRol(@PathVariable("rolNombre") String rolNombre);
}
