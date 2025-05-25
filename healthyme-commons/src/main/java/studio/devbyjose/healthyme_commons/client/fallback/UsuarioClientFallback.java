package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;

@Component
@Slf4j
public class UsuarioClientFallback implements UsuarioClient {

    @Override
    public ResponseEntity<UsuarioDTO> obtenerUsuario(Integer id) {
        log.error("Fallback para getUsuarioById con ID: {}", id);
        return ResponseEntity.notFound().build();
    }
}
