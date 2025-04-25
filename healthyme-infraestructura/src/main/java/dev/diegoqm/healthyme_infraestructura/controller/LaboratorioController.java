package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.LaboratorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    @Autowired
    private LaboratorioService laboratorioService;

    @PostMapping
    public ResponseEntity<LaboratorioDTO> createLaboratorio(@RequestBody LaboratorioDTO dto) {
        return new ResponseEntity<>(laboratorioService.createLaboratorio(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboratorioDTO> getLaboratorioById(@PathVariable int id) {
        return new ResponseEntity<>(laboratorioService.getLaboratorioById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<LaboratorioDTO>> getAllLaboratorios() {
        return new ResponseEntity<>(laboratorioService.getAllLaboratorios(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaboratorioDTO> updateLaboratorio(@PathVariable int id,
                                                            @RequestBody LaboratorioDTO dto) {
        return new ResponseEntity<>(laboratorioService.updateLaboratorio(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLaboratorioById(@PathVariable int id) {
        laboratorioService.deleteLaboratorioById(id);
        return new ResponseEntity<>("Laboratorio eliminado con éxito", HttpStatus.OK);
    }
}

