package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.fallback.PacienteClientFallback;

@FeignClient(name = "healthyme-pacientes", fallback = PacienteClientFallback.class)
public interface PacienteClient {

    /**
     * Obtiene la información básica de un paciente por su ID
     *
     * @param idPaciente ID del paciente
     * @return Datos del paciente (mapa con campos como nombre, email, etc)
     */
    @GetMapping("/api/pacientes/{id}")
    PacienteDTO obtenerPaciente(@PathVariable("id") Integer idPaciente);
}
