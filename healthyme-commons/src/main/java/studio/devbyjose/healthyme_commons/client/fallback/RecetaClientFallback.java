package studio.devbyjose.healthyme_commons.client.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.MedicamentoDTO;
import studio.devbyjose.healthyme_commons.client.dto.RecetaDTO;
import studio.devbyjose.healthyme_commons.client.feign.RecetaClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RecetaClientFallback implements RecetaClient {
    @Override public ResponseEntity<RecetaDTO> obtenerReceta(Integer id) {
        RecetaDTO recetaDefault = RecetaDTO.builder()
                .idReceta(id)
                .nombrePaciente("Paciente")
                .nombreMedico("Dr./Dra.")
                .especialidad("Medicina General")
                .fecha(LocalDate.parse("No disponible"))
                .indicaciones("No disponible")
                .medicamentos(List.of(
                        MedicamentoDTO.builder()
                        .nombre("No disponible")
                        .dosis("No disponible")
                                .indicaciones("No disponible")
                                .build()
                ))
                .build();
        return ResponseEntity.status(503).body(recetaDefault); // 503 Service Unavailable
    } @Override public ResponseEntity<byte[]> obtenerPdfReceta(Integer id) {
        return ResponseEntity.status(503).body(null);
    }
}