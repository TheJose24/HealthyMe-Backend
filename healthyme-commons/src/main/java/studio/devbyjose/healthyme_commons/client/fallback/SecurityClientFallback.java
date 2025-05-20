package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.SecurityClient;

@Component
@Slf4j
public class SecurityClientFallback implements SecurityClient {

    @Override
    public ResponseEntity<UsuarioDTO> getUsuarioById(Integer id) {
        log.error("Fallback para getUsuarioById con ID: {}", id);
        return ResponseEntity.notFound().build();
    }
}