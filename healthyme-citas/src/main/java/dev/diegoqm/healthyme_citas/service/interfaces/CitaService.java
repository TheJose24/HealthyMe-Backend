package dev.diegoqm.healthyme_citas.service.interfaces;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.dto.CitasHoyDTO;
import dev.diegoqm.healthyme_citas.dto.EspecialidadContadaDTO;

import java.util.List;

public interface CitaService {
    // CREATE
    CitaDTO createCita(CitaDTO citaDto);

    // SELECT BY ID
    CitaDTO getCitaById(String id);

    // SELECT ALL
    List<CitaDTO> getAllCitas();

    // UPDATE
    CitaDTO updateCita(String id, CitaDTO citaDto);

    // COUNT
    Long countCitas();

    List<CitasHoyDTO> getCitasDeHoy();

    List<EspecialidadContadaDTO> getEspecialidadesMasSolicitadas();

    // DELETE BY ID
    void deleteCitaById(String id);

}
