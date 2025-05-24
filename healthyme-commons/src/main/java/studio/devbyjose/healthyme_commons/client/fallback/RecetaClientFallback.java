package studio.devbyjose.healthyme_commons.client.fallback;

import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.RecetaDTO;
import studio.devbyjose.healthyme_commons.client.feign.RecetaClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RecetaClientFallback implements RecetaClient {
    @Override
    public Map<String, Object> obtenerReceta(Integer id) {
        Map<String, Object> recetaDefault = new HashMap<>();
        recetaDefault.put("nombrePaciente", "Paciente");
        recetaDefault.put("nombreMedico", "Dr./Dra.");
        recetaDefault.put("especialidad", "Medicina General");
        recetaDefault.put("fecha", "No disponible");
        recetaDefault.put("indicaciones", "No disponible");
        recetaDefault.put("medicamentos", List.of(
                Map.of(
                        "nombre", "No disponible",
                        "dosis", "No disponible",
                        "indicaciones", "No disponible"
                )
        ));
        return recetaDefault;
    }
}