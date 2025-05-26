package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.feign.PacienteClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PacienteClientFallback implements PacienteClient {

    @Override
    public ResponseEntity<List<PacienteDTO>> findAllPacientes() {
        log.error("Fallback: Error al obtener todos los pacientes");
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<PacienteDTO> findPacienteById(String id) {
        log.error("Fallback: Error al obtener paciente con ID {}", id);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<PacienteDTO> findPacienteByIdUsuario(Long idUsuario) {
        log.error("Fallback: Error al obtener paciente con ID de usuario {}", idUsuario);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<PacienteDTO> createPaciente(PacienteDTO pacienteDTO) {
        log.error("Fallback: Error al crear paciente");
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<PacienteDTO> updatePaciente(String id, PacienteDTO pacienteDTO) {
        log.error("Fallback: Error al actualizar paciente con ID {}", id);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<Void> deletePaciente(String id) {
        log.error("Fallback: Error al eliminar paciente con ID {}", id);
        return ResponseEntity.internalServerError().build();
    }
}
