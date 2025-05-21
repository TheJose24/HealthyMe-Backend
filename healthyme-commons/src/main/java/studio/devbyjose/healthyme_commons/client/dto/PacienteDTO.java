package studio.devbyjose.healthyme_commons.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long idUsuario;

    private SeguroDTO seguro;
}