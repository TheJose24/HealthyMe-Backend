package dev.choco.healthyme_laboratorio.dto;

import lombok.Data;
import java.time.*;

@Data
public class ExamenDTO {
    private Integer idExamen;
    private String nombreExamen;
    private String resultados;
    private String observaciones;
    private LocalDate fechaRealizacion;
    private Integer idReservaLab;
    private Integer idLaboratorio;
    private Integer idTecnico;
    private Integer idPaciente;

}
