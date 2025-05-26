package dev.Elmer.healthyme_consultas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MedicamentoDto {

    @NotBlank(message = "El nombre del medicamento no puede estar vacío")
    @Size(max = 100, message = "El nombre del medicamento no debe superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "La dosis no puede estar vacía")
    @Size(max = 100, message = "La dosis no debe superar los 100 caracteres")
    private String dosis;

    @NotBlank(message = "Las indicaciones no pueden estar vacías")
    @Size(max = 255, message = "Las indicaciones no deben superar los 255 caracteres")
    private String indicaciones;

}
