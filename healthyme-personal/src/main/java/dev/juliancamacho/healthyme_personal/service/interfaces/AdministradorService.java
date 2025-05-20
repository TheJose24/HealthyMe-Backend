package dev.juliancamacho.healthyme_personal.service.interfaces;

import dev.juliancamacho.healthyme_personal.dto.AdministradorDto;

import java.util.List;

public interface AdministradorService
{

    // CREATE
    AdministradorDto createAdministrador(AdministradorDto administradorDto);

    // SELECT BY ID
    AdministradorDto getAdministradorById(Integer id);

    // SELECT ALL
    List<AdministradorDto> getAllAdministrador();

    // UPDATE
    AdministradorDto updateAdministrador(Integer id, AdministradorDto administradorDto);

    // DELETE BY ID
    void deleteAdministradorById(Integer id);
}
