package dev.choco.healthyme_laboratorio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "examenes")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idExamen;

    private String nombreExamen;
    private String resultados;
    private String observaciones;
    private LocalDate fechaRealizacion;

    private Integer idLaboratorio;
    private Integer idTecnico;
    private Integer idPaciente;

    @ManyToOne
    @JoinColumn(name = "id_reserva_lab", nullable = false)
    private ReservaLab reservaLab;
}
