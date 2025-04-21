package dev.juliancamacho.healthyme_personal.controller;

import dev.juliancamacho.healthyme_personal.dto.EspecialidadDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    // CREATE
    @PostMapping()
    public ResponseEntity<EspecialidadDto> createEspecialidad(@RequestBody EspecialidadDto especialidadDto) {
        return new ResponseEntity<>(especialidadService.createEspecialidad(especialidadDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDto> getEspecialidadById(@PathVariable int id) {
        return new ResponseEntity<>(especialidadService.getEspecialidadById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @GetMapping()
    public ResponseEntity<List<EspecialidadDto>> getAllEspecialidades() {
        List<EspecialidadDto> especialidades = especialidadService.getAllEspecialidades();
        return new ResponseEntity<>(especialidades, HttpStatus.OK);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDto> updateEspecialidad(@PathVariable int id, @RequestBody EspecialidadDto especialidadDto) {
        return new ResponseEntity<>(especialidadService.updateEspecialidad(id, especialidadDto), HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEspecialidadById(@PathVariable int id) {
        especialidadService.deleteEspecialidadById(id);
        return new ResponseEntity<>("Especialidad eliminada con exito", HttpStatus.OK);
    }
}
