package dev.choco.healthyme_laboratorio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.*;

@Data
public class ExamenDTO {
    private Integer idExamen;

    @NotBlank(message = "El nombre del examen es obligatorio")
    @Size(max = 100, message = "El nombre del examen no debe superar los 100 caracteres")
    private String nombreExamen;

    @Size(max = 500, message = "Los resultados no deben superar los 500 caracteres")
    private String resultados;

    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    private String observaciones;

    @NotNull(message = "La fecha de realización es obligatoria")
    private LocalDate fechaRealizacion;

    @NotNull(message = "El ID de reserva de laboratorio es obligatorio")
    private Integer idReservaLab;

    @NotNull(message = "El ID del laboratorio es obligatorio")
    private Integer idLaboratorio;

    @NotNull(message = "El ID del técnico es obligatorio")
    private Integer idTecnico;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Integer idPaciente;

}
