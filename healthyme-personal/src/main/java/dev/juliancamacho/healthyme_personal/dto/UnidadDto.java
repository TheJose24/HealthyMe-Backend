package dev.juliancamacho.healthyme_personal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UnidadDto {

    private Integer idUnidad;

    @NotBlank(message = "El nombre de la unidad es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String nombreUnidad;

    @Pattern(regexp = ".*\\.(jpg|png)", message = "La imagen debe ser JPG o PNG")
    private String imgUnidad;
}
