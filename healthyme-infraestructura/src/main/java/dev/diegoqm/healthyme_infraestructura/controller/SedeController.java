package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.SedeDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @PostMapping
    public ResponseEntity<SedeDTO> createSede(@RequestBody SedeDTO dto) {
        return new ResponseEntity<>(sedeService.createSede(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SedeDTO> getSedeById(@PathVariable int id) {
        return new ResponseEntity<>(sedeService.getSedeById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SedeDTO>> getAllSedes() {
        return new ResponseEntity<>(sedeService.getAllSedes(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SedeDTO> updateSede(@PathVariable int id,
                                              @RequestBody SedeDTO dto) {
        return new ResponseEntity<>(sedeService.updateSede(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSedeById(@PathVariable int id) {
        sedeService.deleteSedeById(id);
        return new ResponseEntity<>("Sede eliminada con éxito", HttpStatus.OK);
    }
}