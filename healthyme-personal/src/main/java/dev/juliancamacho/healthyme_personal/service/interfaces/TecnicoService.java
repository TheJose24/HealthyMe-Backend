package dev.juliancamacho.healthyme_personal.service.interfaces;

import dev.juliancamacho.healthyme_personal.dto.TecnicoDto;

import java.util.List;

public interface TecnicoService {

    // CREATE
    TecnicoDto createTecnico(TecnicoDto tecnicoDto);

    // SELECT BY ID
    TecnicoDto getTecnicoById(Integer id);

    // SELECT ALL
    List<TecnicoDto> getAllTecnico();

    // UPDATE
    TecnicoDto updateTecnico(Integer id, TecnicoDto tecnicoDto);

    // DELETE BY ID
    void deleteTecnicoById(Integer id);
}
