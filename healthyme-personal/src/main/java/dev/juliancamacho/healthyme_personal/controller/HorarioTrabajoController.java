package dev.juliancamacho.healthyme_personal.controller;

import dev.juliancamacho.healthyme_personal.dto.HorarioTrabajoDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.HorarioTrabajoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horario-trabajo")
public class HorarioTrabajoController {

    @Autowired
    private HorarioTrabajoService horarioTrabajoService;

    // CREATE
    @PostMapping()
    public ResponseEntity<HorarioTrabajoDto> createHorarioTrabajo(@RequestBody HorarioTrabajoDto horarioTrabajoDto) {
        return new ResponseEntity<>(horarioTrabajoService.createHorarioTrabajo(horarioTrabajoDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDto> getHorarioTrabajoById(@PathVariable int id) {
        return new ResponseEntity<>(horarioTrabajoService.getHorarioTrabajoById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @GetMapping()
    public ResponseEntity<List<HorarioTrabajoDto>> getAllHorarioTrabajo() {
        List<HorarioTrabajoDto> horarioTrabajoes = horarioTrabajoService.getAllHorarioTrabajo();
        return new ResponseEntity<>(horarioTrabajoes, HttpStatus.OK);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDto> updateHorarioTrabajo(@PathVariable int id, @RequestBody HorarioTrabajoDto horarioTrabajoDto) {
        return new ResponseEntity<>(horarioTrabajoService.updateHorarioTrabajo(id, horarioTrabajoDto), HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHorarioTrabajoById(@PathVariable int id) {
        horarioTrabajoService.deleteHorarioTrabajoById(id);
        return new ResponseEntity<>("HorarioTrabajo eliminada con exito", HttpStatus.OK);
    }
}
