package dev.diegoqm.healthyme_citas.service.interfaces;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import java.util.List;

public interface CitaService {
    // CREATE
    CitaDTO createCita(CitaDTO citaDto);

    // SELECT BY ID
    CitaDTO getCitaById(int id);

    // SELECT ALL
    List<CitaDTO> getAllCitas();

    // UPDATE
    CitaDTO updateCita(int id, CitaDTO citaDto);

    // DELETE BY ID
    void deleteCitaById(int id);

}
