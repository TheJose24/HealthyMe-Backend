package dev.Elmer.healthyme_consultas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Integer idConsulta;

    @Column(name = "sintomas")
    private String sintomas;

    @Column(name = "diagnostico")
    private String diagnostico;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "id_cita")
    private Integer idCita;

    @Column(name = "id_paciente")
    private Integer idPaciente;

    @Column(name = "id_medico")
    private Integer idMedico;

}

