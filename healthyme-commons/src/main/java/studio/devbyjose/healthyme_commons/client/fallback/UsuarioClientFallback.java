package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;

import java.util.List;

@Component
@Slf4j
public class UsuarioClientFallback implements UsuarioClient {

    @Override
    public ResponseEntity<UsuarioDTO> obtenerUsuario(Integer id) {
        log.error("Fallback para getUsuarioById con ID: {}", id);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorDni(String dni) {
        log.error("Fallback para getUsuarioByDni con DNI: {}", dni);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorUsername(String username) {
        log.error("Fallback para getUsuarioByUsername con Username: {}", username);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<UsuarioDTO>> obtenerUsuariosPorRol(String rolNombre) {
        log.error("Fallback para getUsuariosByRol con Rol: {}", rolNombre);
        return ResponseEntity.notFound().build();
    }
}
