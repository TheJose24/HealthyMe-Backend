package dev.diegoqm.healthyme_citas.controller;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.service.interfaces.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {
    @Autowired
    private CitaService citaService;

    // CREATE
    @PostMapping
    public ResponseEntity<CitaDTO> createCita(@RequestBody CitaDTO citaDTO) {
        CitaDTO nueva = citaService.createCita(citaDTO);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    // SELECT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> getCitaById(@PathVariable int id) {
        CitaDTO cita = citaService.getCitaById(id);
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

    // SELECT ALL
    @GetMapping
    public ResponseEntity<List<CitaDTO>> getAllCitas() {
        List<CitaDTO> citas = citaService.getAllCitas();
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CitaDTO> updateCita(@PathVariable int id,
                                              @RequestBody CitaDTO citaDTO) {
        CitaDTO actualizado = citaService.updateCita(id, citaDTO);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCitaById(@PathVariable int id) {
        citaService.deleteCitaById(id);
        return new ResponseEntity<>("Cita eliminada con éxito", HttpStatus.OK);
    }
}
