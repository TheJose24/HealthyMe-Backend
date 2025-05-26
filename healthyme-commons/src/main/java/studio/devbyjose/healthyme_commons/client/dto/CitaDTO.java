package studio.devbyjose.healthyme_commons.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import studio.devbyjose.healthyme_commons.enums.citas.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {

    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;
    private Integer idPaciente;
    private Integer idMedico;
    private Integer idConsultorio;

}