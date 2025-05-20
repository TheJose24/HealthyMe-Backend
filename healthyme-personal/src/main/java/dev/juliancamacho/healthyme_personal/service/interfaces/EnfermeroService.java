package dev.juliancamacho.healthyme_personal.service.interfaces;

import dev.juliancamacho.healthyme_personal.dto.EnfermeroDto;

import java.util.List;

public interface EnfermeroService {

    // CREATE
    EnfermeroDto createEnfermero(EnfermeroDto enfermeroDto);

    // SELECT BY ID
    EnfermeroDto getEnfermeroById(Integer id);

    // SELECT ALL
    List<EnfermeroDto> getAllEnfermero();

    // UPDATE
    EnfermeroDto updateEnfermero(Integer id, EnfermeroDto enfermeroDto);

    // DELETE BY ID
    void deleteEnfermeroById(Integer id);
}
