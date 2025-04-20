package studio.devbyjose.healthyme_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_notification.enums.TipoPlantilla;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantillaDTO {
    private Integer idPlantilla;
    private TipoPlantilla tipo;
    private String nombre;
    private String asunto;
    private String variables;
}