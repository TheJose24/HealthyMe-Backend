package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.fallback.PacienteClientFallback;

import java.util.List;

@FeignClient(name = "healthyme-pacientes", fallback = PacienteClientFallback.class)
public interface PacienteClient {
    
    @GetMapping("/api/v1/pacientes")
    ResponseEntity<List<PacienteDTO>> findAllPacientes();
    
    @GetMapping("/api/v1/pacientes/{id}")
    ResponseEntity<PacienteDTO> findPacienteById(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/pacientes/usuario/{idUsuario}")
    ResponseEntity<PacienteDTO> findPacienteByIdUsuario(@PathVariable("idUsuario") Long idUsuario);
    
    @PostMapping("/api/v1/pacientes")
    ResponseEntity<PacienteDTO> createPaciente(@RequestBody PacienteDTO pacienteDTO);
    
    @PutMapping("/api/v1/pacientes/{id}")
    ResponseEntity<PacienteDTO> updatePaciente(@PathVariable("id") String id, @RequestBody PacienteDTO pacienteDTO);
    
    @DeleteMapping("/api/v1/pacientes/{id}")
    ResponseEntity<Void> deletePaciente(@PathVariable("id") String id);
}
