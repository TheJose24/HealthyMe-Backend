package dev.juliancamacho.healthyme_personal.service.interfaces;

import dev.juliancamacho.healthyme_personal.dto.HorarioTrabajoDto;

import java.util.List;

public interface HorarioTrabajoService {
    // CREATE
    HorarioTrabajoDto createHorarioTrabajo(HorarioTrabajoDto horarioTrabajoDto);

    // SELECT BY ID
    HorarioTrabajoDto getHorarioTrabajoById(Integer id);

    // SELECT ALL
    List<HorarioTrabajoDto> getAllHorarioTrabajo();

    // UPDATE
    HorarioTrabajoDto updateHorarioTrabajo(Integer id, HorarioTrabajoDto horarioTrabajoDto);

    // DELETE BY ID
    void deleteHorarioTrabajoById(Integer id);
}
