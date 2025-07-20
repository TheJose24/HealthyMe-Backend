package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.CitaDTO;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorDiaDTO;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorEspecialidadDTO;
import studio.devbyjose.healthyme_commons.client.dto.EspecialidadContadaDTO;
import studio.devbyjose.healthyme_commons.client.feign.CitaClient;
import studio.devbyjose.healthyme_commons.enums.citas.EstadoCita;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class CitaClientFallback implements CitaClient {

    @Override
    public CitaDTO createCita(CitaDTO citaDTO) {
        log.error("⚠️ Fallback: createCita failed for payload={}", citaDTO);
        return CitaDTO.builder().build();
    }

    @Override
    public CitaDTO getCitaById(Integer id) {
        log.error("⚠️ Fallback: getCitaById failed for id={}", id);
        return CitaDTO.builder().id(id).build();
    }

    @Override
    public List<CitaDTO> getAllCitas() {
        log.error("⚠️ Fallback: getAllCitas failed");
        return Collections.emptyList();
    }

    @Override
    public CitaDTO updateCita(Integer id, CitaDTO citaDTO) {
        log.error("⚠️ Fallback: updateCita failed for id={}, payload={}", id, citaDTO);
        return CitaDTO.builder().id(id).build();
    }

    @Override
    public void deleteCitaById(Integer id) {
        log.error("⚠️ Fallback: deleteCitaById failed for id={}", id);
    }

    @Override
    public Long getTotalCitas() {
        return 0L;
    }

    @Override
    public List<EspecialidadContadaDTO> getEspecialidadesMasSolicitadas() {
        return List.of();
    }

    @Override
    public Long getCitasByEstado(EstadoCita estado) {
        return 0L;
    }

    @Override
    public Long getCitasEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return 0L;
    }

    @Override
    public Long getCitasByEstadoEnRango(EstadoCita estado, LocalDate fechaInicio, LocalDate fechaFin) {
        return 0L;
    }

    @Override
    public List<CitasPorDiaDTO> getCitasPorDiaEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return List.of();
    }
}

