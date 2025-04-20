package dev.juliancamacho.healthyme_personal.service.interfaces;

import dev.juliancamacho.healthyme_personal.dto.EspecialidadDto;

import java.util.List;

public interface EspecialidadService {

    // CREATE
    EspecialidadDto createEspecialidad(EspecialidadDto especialidadDto);

    // SELECT BY ID
    EspecialidadDto getEspecialidadById(int id);

    // SELECT ALL
    List<EspecialidadDto> getAllEspecialidades();

    // UPDATE
    EspecialidadDto updateEspecialidad(int id, EspecialidadDto especialidadDto);

    // DELETE BY ID
    void deleteEspecialidadById(int id);

}
