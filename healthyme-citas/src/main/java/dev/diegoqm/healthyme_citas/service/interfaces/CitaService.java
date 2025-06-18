package dev.diegoqm.healthyme_citas.service.interfaces;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
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

    // DELETE BY ID
    void deleteCitaById(String id);

}
