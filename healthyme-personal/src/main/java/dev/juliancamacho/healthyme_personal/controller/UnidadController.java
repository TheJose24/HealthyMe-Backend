package dev.juliancamacho.healthyme_personal.controller;

import dev.juliancamacho.healthyme_personal.dto.UnidadDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.UnidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadController {

    @Autowired
    private UnidadService unidadService;

    // CREATE
    @PostMapping()
    public ResponseEntity<UnidadDto> createUnidada(@RequestBody UnidadDto unidadDto) {
        return new ResponseEntity<>(unidadService.createUnidad(unidadDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<UnidadDto> getUnidadaById(@PathVariable int id) {
        return new ResponseEntity<>(unidadService.getUnidadById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @GetMapping()
    public ResponseEntity<List<UnidadDto>> getAllUnidadaes() {
        List<UnidadDto> unidades = unidadService.getAllUnidades();
        return new ResponseEntity<>(unidades, HttpStatus.OK);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<UnidadDto> updateUnidada(@PathVariable int id, @RequestBody UnidadDto unidadDto) {
        return new ResponseEntity<>(unidadService.updateUnidad(id, unidadDto), HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUnidadaById(@PathVariable int id) {
        unidadService.deleteUnidadById(id);
        return new ResponseEntity<>("Unidad eliminada con exito", HttpStatus.OK);
    }
}
