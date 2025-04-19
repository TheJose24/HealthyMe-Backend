package studio.devbyjose.healthyme_commons.client.fallback;

import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.feign.PacienteClient;

@Component
public class PacienteClientFallback implements PacienteClient {
    @Override
    public PacienteDTO obtenerPaciente(Integer id) {
        return PacienteDTO.builder()
                .nombre("Paciente")
                .apellido("No Disponible")
                .email("no-disponible@example.com")
                .build();
    }
}
