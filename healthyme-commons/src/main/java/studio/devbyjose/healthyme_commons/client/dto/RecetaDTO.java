package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetaDTO {

    private Integer idReceta;
    private String medicamento;
    private String dosis;
    private Integer instrucciones;
    private LocalDate fechaEmision;
}
