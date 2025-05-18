package studio.devbyjose.healthyme_pacientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriajeDTO {
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser positivo")
    private BigDecimal peso;

    @NotNull(message = "La talla es obligatoria")
    @Positive(message = "La talla debe ser positiva")
    private BigDecimal talla;

    @NotBlank(message = "Las alergias son obligatorias")
    private String alergias;

    @NotBlank(message = "Las condiciones médicas son obligatorias")
    private String condicionesMedicas;

    @NotBlank(message = "Los antecedentes familiares son obligatorios")
    private String antecedentesFamiliares;

    @NotNull(message = "La presión arterial es obligatoria")
    private BigDecimal presionArterial;

    @NotNull(message = "La frecuencia cardíaca es obligatoria")
    private BigDecimal frecuenciaCardiaca;

    @NotNull(message = "La frecuencia respiratoria es obligatoria")
    private BigDecimal frecuenciaRespiratoria;

    @NotNull(message = "El ID de paciente es obligatorio")
    private Long idPaciente;
}