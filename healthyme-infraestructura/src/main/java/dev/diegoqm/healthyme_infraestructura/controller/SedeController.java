package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.SedeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sedes", description = "API para gestionar sedes")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeService sedeService;

    @Operation(summary = "Crear una nueva sede")
    @PostMapping
    public ResponseEntity<SedeDTO> createSede(@Valid @RequestBody SedeDTO dto) {
        return new ResponseEntity<>(sedeService.createSede(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una sede por ID")
    @GetMapping("/{id}")
    public ResponseEntity<SedeDTO> getSedeById(@PathVariable int id) {
        return new ResponseEntity<>(sedeService.getSedeById(id), HttpStatus.OK);
    }

    @Operation(summary = "Obtener la lista de todas las sedes")
    @GetMapping
    public ResponseEntity<List<SedeDTO>> getAllSedes() {
        return new ResponseEntity<>(sedeService.getAllSedes(), HttpStatus.OK);
    }

    @Operation(summary = "Actualizar una sede existente")
    @PutMapping("/{id}")
    public ResponseEntity<SedeDTO> updateSede(@PathVariable int id,
                                              @Valid @RequestBody SedeDTO dto) {
        return new ResponseEntity<>(sedeService.updateSede(id, dto), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar una sede por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSedeById(@PathVariable int id) {
        sedeService.deleteSedeById(id);
        return new ResponseEntity<>("Sede eliminada con éxito", HttpStatus.OK);
    }
}