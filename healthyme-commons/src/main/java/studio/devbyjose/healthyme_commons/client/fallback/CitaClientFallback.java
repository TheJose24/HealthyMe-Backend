package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.CitaDTO;
import studio.devbyjose.healthyme_commons.client.feign.CitaClient;

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
}

