package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.ConsultorioDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.ConsultorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultorios")
public class ConsultorioController {

    @Autowired
    private ConsultorioService service;

    @PostMapping
    public ResponseEntity<ConsultorioDTO> createConsultorio(@RequestBody ConsultorioDTO dto) {
        return new ResponseEntity<>(service.createConsultorio(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultorioDTO> getConsultorioById(@PathVariable int id) {
        return new ResponseEntity<>(service.getConsultorioById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ConsultorioDTO>> getAllConsultorios() {
        return new ResponseEntity<>(service.getAllConsultorios(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultorioDTO> updateConsultorio(@PathVariable int id,
                                                            @RequestBody ConsultorioDTO dto) {
        return new ResponseEntity<>(service.updateConsultorio(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConsultorioById(@PathVariable int id) {
        service.deleteConsultorioById(id);
        return new ResponseEntity<>("Consultorio eliminado con éxito", HttpStatus.OK);
    }
}
