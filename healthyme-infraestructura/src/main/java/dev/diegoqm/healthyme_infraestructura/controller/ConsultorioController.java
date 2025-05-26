package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.ConsultorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Consultorios", description = "API para gestionar consultorios")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consultorios")
public class ConsultorioController {

    private final ConsultorioService service;

    @Operation(summary = "Crear un nuevo consultorio")
    @PostMapping
    public ResponseEntity<ConsultorioDTO> createConsultorio(@Valid @RequestBody ConsultorioDTO dto) {
        return new ResponseEntity<>(service.createConsultorio(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un consultorio por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<ConsultorioDTO> getConsultorioById(@PathVariable int id) {
        return new ResponseEntity<>(service.getConsultorioById(id), HttpStatus.OK);
    }

    @Operation(summary = "Obtener la lista de todos los consultorios")
    @GetMapping
    public ResponseEntity<List<ConsultorioDTO>> getAllConsultorios() {
        return new ResponseEntity<>(service.getAllConsultorios(), HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un consultorio existente")
    @PutMapping("/{id}")
    public ResponseEntity<ConsultorioDTO> updateConsultorio(@PathVariable int id,
                                                            @Valid @RequestBody ConsultorioDTO dto) {
        return new ResponseEntity<>(service.updateConsultorio(id, dto), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un consultorio por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConsultorioById(@PathVariable int id) {
        service.deleteConsultorioById(id);
        return new ResponseEntity<>("Consultorio eliminado con éxito", HttpStatus.OK);
    }
}