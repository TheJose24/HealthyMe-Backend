package dev.diegoqm.healthyme_citas.service.interfaces;

import dev.diegoqm.healthyme_citas.dto.CitaDTO;
import dev.diegoqm.healthyme_citas.dto.CitasHoyDTO;
import dev.diegoqm.healthyme_citas.dto.EspecialidadContadaDTO;
import dev.diegoqm.healthyme_citas.enums.EstadoCita;

import java.util.List;

import org.springdoc.core.converters.models.Pageable;

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

    CitaDTO findNextCitaByPaciente(Long idPaciente);

    Long countByPacienteAndEstado(Long idPaciente, EstadoCita estado);

    List<CitaDTO> findUltimasCitasByPaciente(Long idPaciente, int size);

    CitaDTO findNextCitaByUsuario(Long usuarioId);
    Long   countByUsuarioAndEstado(Long usuarioId, EstadoCita estado);
    List<CitaDTO> findUltimasByUsuario(Long usuarioId, int size);

    List<CitaDTO> findAllByUsuario(Long usuarioId);
    List<CitaDTO> findByUsuarioAndEstado(Long usuarioId, EstadoCita estado);

    // DELETE BY ID
    void deleteCitaById(String id);

}
