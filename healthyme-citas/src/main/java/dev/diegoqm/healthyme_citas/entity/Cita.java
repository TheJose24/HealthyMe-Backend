package dev.diegoqm.healthyme_citas.entity;

import dev.diegoqm.healthyme_citas.enums.EstadoCita;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "id_medico", nullable = false)
    private Integer idMedico;

    @Column(name = "id_consultorio")
    private Integer idConsultorio;

}