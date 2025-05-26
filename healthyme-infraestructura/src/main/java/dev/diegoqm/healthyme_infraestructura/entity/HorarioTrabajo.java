package dev.diegoqm.healthyme_infraestructura.entity;

import dev.diegoqm.healthyme_infraestructura.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "horario_trabajo")
public class HorarioTrabajo extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
}
