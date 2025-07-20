package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_commons.client.dto.CitaDTO;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorDiaDTO;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorEspecialidadDTO;
import studio.devbyjose.healthyme_commons.client.dto.EspecialidadContadaDTO;
import studio.devbyjose.healthyme_commons.client.fallback.CitaClientFallback;
import studio.devbyjose.healthyme_commons.enums.citas.EstadoCita;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "healthyme-citas", fallback = CitaClientFallback.class)
public interface CitaClient {

    @PostMapping("/api/v1/citas")
    CitaDTO createCita(@RequestBody CitaDTO citaDTO);

    @GetMapping("/api/v1/citas/{id}")
    CitaDTO getCitaById(@PathVariable("id") Integer id);

    @GetMapping("/api/v1/citas")
    List<CitaDTO> getAllCitas();

    @PutMapping("/api/v1/citas/{id}")
    CitaDTO updateCita(@PathVariable("id") Integer id,
                       @RequestBody CitaDTO citaDTO);

    @DeleteMapping("/api/v1/citas/{id}")
    void deleteCitaById(@PathVariable("id") Integer id);

    @GetMapping("/api/v1/citas/count")
    Long getTotalCitas();

    @GetMapping("/api/v1/citas/especialidades/mas-solicitadas")
    List<EspecialidadContadaDTO> getEspecialidadesMasSolicitadas();

    @GetMapping("/api/v1/citas/estado/{estado}")
    Long getCitasByEstado(@PathVariable("estado") EstadoCita estado);

    @GetMapping("/api/v1/citas/rango")
    Long getCitasEnRango(@RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                         @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin);

    @GetMapping("/api/v1/citas/estado/{estado}/rango")
    Long getCitasByEstadoEnRango(@PathVariable("estado") EstadoCita estado,
                                 @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                 @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin);

    @GetMapping("/api/v1/citas/por-dia")
    List<CitasPorDiaDTO> getCitasPorDiaEnRango(@RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                               @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin);
}
