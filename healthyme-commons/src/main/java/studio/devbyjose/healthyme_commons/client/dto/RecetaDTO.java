package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetaDTO {

    private Integer idReceta;
    private LocalDate fechaEmision;
    private List<MedicamentoDTO> medicamentos; // si lo vas a usar en reportes, PDFs, etc.
}
