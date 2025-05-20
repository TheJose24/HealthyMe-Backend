package studio.devbyjose.healthyme_commons.client.fallback;

import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;

@Component
public class UsuarioClientFallback implements UsuarioClient {

    @Override
    public UsuarioDTO obtenerUsuario(Integer id) {
        return UsuarioDTO.builder().build();
    }
}
