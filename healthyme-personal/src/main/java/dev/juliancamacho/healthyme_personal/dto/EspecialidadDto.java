package dev.juliancamacho.healthyme_personal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EspecialidadDto {

    private Integer idEspecialidad;

    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String nombreEspecialidad;

    @Pattern(regexp = "^(?i).+\\.(jpg|png)$", message = "La imagen debe ser JPG o PNG")
    private String imgEspecialidad;
}
