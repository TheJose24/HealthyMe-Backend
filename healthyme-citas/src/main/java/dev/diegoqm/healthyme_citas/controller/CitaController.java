package dev.diegoqm.healthyme_citas.controller;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.dto.CitasHoyDTO;
import dev.diegoqm.healthyme_citas.dto.EspecialidadContadaDTO;
import dev.diegoqm.healthyme_citas.service.interfaces.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Citas", description = "API para gestionar citas")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "http://localhost:4200")
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


    @Operation(summary = "Eliminar una cita por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCitaById(@PathVariable String id) {
        citaService.deleteCitaById(id);
        return new ResponseEntity<>("Cita eliminada con éxito", HttpStatus.OK);
    }
}