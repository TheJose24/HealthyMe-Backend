package studio.devbyjose.healthyme_commons.client.fallback;

import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.MedicoDTO;
import studio.devbyjose.healthyme_commons.client.feign.MedicoClient;

@Component
public class MedicoClientFallback implements MedicoClient {
    @Override
    public MedicoDTO obtenerMedico(Integer id) {
        return MedicoDTO.builder()
                .nombre("Dr.")
                .apellido("No Disponible")
                .especialidad("Medicina General")
                .build();
    }
}