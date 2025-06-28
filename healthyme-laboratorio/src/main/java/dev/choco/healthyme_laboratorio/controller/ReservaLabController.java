package dev.choco.healthyme_laboratorio.controller;

import dev.choco.healthyme_laboratorio.dto.ReservaLabDTO;
import dev.choco.healthyme_laboratorio.entity.ReservaLab;
import dev.choco.healthyme_laboratorio.service.Interfaces.ReservaLabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Reservas de Laboratorio", description = "Gestión de reservas para exámenes de laboratorio")

public class ReservaLabController {

    private final ReservaLabService service;

    @Operation(
            summary = "Registrar nueva reserva",
            description = "Crea una nueva reserva de laboratorio para un paciente"
    )
    @PostMapping
    public ResponseEntity<ReservaLabDTO> guardar(@Valid @RequestBody ReservaLabDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @Operation(
            summary = "Listar todas las reservas",
            description = "Obtiene una lista de todas las reservas registradas en el sistema"
    )
    @GetMapping
    public ResponseEntity<List<ReservaLabDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(
            summary = "Obtener una reserva por ID",
            description = "Obtiene los detalles de una reserva de laboratorio usando su ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ReservaLabDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(
            summary = "Actualizar una reserva existente",
            description = "Actualiza los datos de una reserva existente en el sistema"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ReservaLabDTO> actualizar(@PathVariable Integer id,@Valid @RequestBody ReservaLabDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Obtener el número total de reservas de laboratorio")
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalReservas() {
        return ResponseEntity.ok(service.countReservas());
    }

    @Operation(
            summary = "Eliminar una reserva",
            description = "Elimina una reserva de laboratorio por su ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
