package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.HorarioTrabajoDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.HorarioTrabajoService;
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

    @PostMapping
    public ResponseEntity<HorarioTrabajoDTO> createHorarioTrabajo(@RequestBody HorarioTrabajoDTO dto) {
        return new ResponseEntity<>(horarioTrabajoService.createHorarioTrabajo(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDTO> getHorarioTrabajoById(@PathVariable int id) {
        return new ResponseEntity<>(horarioTrabajoService.getHorarioTrabajoById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<HorarioTrabajoDTO>> getAllHorarioTrabajo() {
        return new ResponseEntity<>(horarioTrabajoService.getAllHorarioTrabajo(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDTO> updateHorarioTrabajo(@PathVariable int id,
                                                                  @RequestBody HorarioTrabajoDTO dto) {
        return new ResponseEntity<>(horarioTrabajoService.updateHorarioTrabajo(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHorarioTrabajoById(@PathVariable int id) {
        horarioTrabajoService.deleteHorarioTrabajoById(id);
        return new ResponseEntity<>("Horario eliminado con éxito", HttpStatus.OK);
    }
}