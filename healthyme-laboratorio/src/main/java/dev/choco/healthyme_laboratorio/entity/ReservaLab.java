package dev.choco.healthyme_laboratorio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;


@Entity
@Table(name = "reserva_lab")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReservaLab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReservaLab;

    private LocalDate fecha;
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    private Integer idPaciente;
    private Integer idTecnico;
    private Integer idLaboratorio;

    public enum EstadoReserva {
        PENDIENTE, REALIZADA, CANCELADA
    }
}

