package dev.diegoqm.healthyme_citas.controller;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.dto.CitasHoyDTO;
import dev.diegoqm.healthyme_citas.dto.EspecialidadContadaDTO;
import dev.diegoqm.healthyme_citas.enums.EstadoCita;
import dev.diegoqm.healthyme_citas.service.interfaces.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Citas", description = "API para gestionar citas")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    private final CitaService citaService;

    @Operation(summary = "Crear una nueva cita")
    @PostMapping
    public ResponseEntity<CitaDTO> createCita(@Valid @RequestBody CitaDTO citaDTO) {
        CitaDTO nueva = citaService.createCita(citaDTO);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una cita por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> getCitaById(@PathVariable String id) {
        CitaDTO cita = citaService.getCitaById(id);
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

    @Operation(summary = "Obtener la lista de todas las citas")
    @GetMapping
    public ResponseEntity<List<CitaDTO>> getAllCitas() {
        List<CitaDTO> citas = citaService.getAllCitas();
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar una cita existente")
    @PutMapping("/{id}")
    public ResponseEntity<CitaDTO> updateCita(@PathVariable String id,
                                              @Valid @RequestBody CitaDTO citaDTO) {
        CitaDTO actualizado = citaService.updateCita(id, citaDTO);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el número total de citas")

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCitas() {
        return ResponseEntity.ok(citaService.countCitas());
    }

    @GetMapping("/hoy")
    @Operation(summary = "Obtener citas de hoy", description = "Retorna las citas del día actual con nombre del médico, especialidad y hora de inicio")
    public ResponseEntity<List<CitasHoyDTO>> getCitasDeHoy() {
        List<CitasHoyDTO> citas = citaService.getCitasDeHoy();
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/especialidades/mas-solicitadas")
    @Operation(summary = "Obtener especialidades más solicitadas", description = "Devuelve la cantidad de citas por especialidad")
    public ResponseEntity<List<EspecialidadContadaDTO>> getEspecialidadesMasSolicitadas() {
        return ResponseEntity.ok(citaService.getEspecialidadesMasSolicitadas());
    }

    @GetMapping("/paciente/{id}/proxima")
    public ResponseEntity<CitaDTO> getNextCita(@PathVariable("id") Long idPaciente) {
        CitaDTO dto = citaService.findNextCitaByPaciente(idPaciente);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/paciente/{id}/count")
    public ResponseEntity<Map<String, Long>> getCounts(@PathVariable("id") Long idPaciente) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("pendientes",  citaService.countByPacienteAndEstado(idPaciente, EstadoCita.PENDIENTE));
        counts.put("confirmadas", citaService.countByPacienteAndEstado(idPaciente, EstadoCita.REALIZADA));
        counts.put("cumplidas",   citaService.countByPacienteAndEstado(idPaciente, EstadoCita.CANCELADA));
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/paciente/{id}/ultimas")
    public ResponseEntity<List<CitaDTO>> getUltimasCitas(
            @PathVariable("id") Long idPaciente,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<CitaDTO> lista = citaService.findUltimasCitasByPaciente(idPaciente, size);
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Próxima cita de un usuario")
    @GetMapping("/usuario/{usuarioId}/proxima")
    public ResponseEntity<CitaDTO> proximaPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(
                citaService.findNextCitaByUsuario(usuarioId)
        );
    }

    @Operation(summary = "Conteos de citas por estado para un usuario")
    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<Map<String, Long>> countsPorUsuario(@PathVariable Long usuarioId) {
        Map<String, Long> m = new HashMap<>();
        m.put("pendientes",
                citaService.countByUsuarioAndEstado(usuarioId, EstadoCita.PENDIENTE));
        m.put("realizadas",
                citaService.countByUsuarioAndEstado(usuarioId, EstadoCita.REALIZADA));
        m.put("canceladas",
                citaService.countByUsuarioAndEstado(usuarioId, EstadoCita.CANCELADA));
        return ResponseEntity.ok(m);
    }

    @Operation(summary = "Últimas N citas de un usuario")
    @GetMapping("/usuario/{usuarioId}/ultimas")
    public ResponseEntity<List<CitaDTO>> ultimasPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(
                citaService.findUltimasByUsuario(usuarioId, size)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CitaDTO>> getAllByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(citaService.findAllByUsuario(usuarioId));
    }


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CitaDTO>> getByUsuarioAndEstado(
            @PathVariable Long usuarioId,
            @RequestParam EstadoCita estado) {
        return ResponseEntity.ok(
                citaService.findByUsuarioAndEstado(usuarioId, estado)
        );
    }

    @Operation(summary = "Eliminar una cita por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCitaById(@PathVariable String id) {
        citaService.deleteCitaById(id);
        return new ResponseEntity<>("Cita eliminada con éxito", HttpStatus.OK);
    }
}