package dev.juliancamacho.healthyme_personal.service.interfaces;

import dev.juliancamacho.healthyme_personal.dto.UnidadDto;

import java.util.List;

public interface UnidadService
{
    // CREATE
    UnidadDto createUnidad(UnidadDto unidadDto);

    // SELECT BY ID
    UnidadDto getUnidadById(Integer id);

    // SELECT ALL
    List<UnidadDto> getAllUnidades();

    // UPDATE
    UnidadDto updateUnidad(Integer id, UnidadDto unidadDto);

    // DELETE BY ID
    void deleteUnidadById(Integer id);
}
