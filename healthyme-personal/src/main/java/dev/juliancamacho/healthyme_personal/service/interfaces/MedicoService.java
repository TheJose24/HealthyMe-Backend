package dev.juliancamacho.healthyme_personal.service.interfaces;

import dev.juliancamacho.healthyme_personal.dto.MedicoDto;

import java.util.List;

public interface MedicoService
{
    // CREATE
    MedicoDto createMedico(MedicoDto medicoDto);

    // SELECT BY ID
    MedicoDto getMedicoById(Integer id);

    // SELECT ALL
    List<MedicoDto> getAllMedico();

    // UPDATE
    MedicoDto updateMedico(Integer id, MedicoDto medicoDto);

    // DELETE BY ID
    void deleteMedicoById(Integer id);
}
