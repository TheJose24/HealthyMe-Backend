package dev.Elmer.healthyme_consultas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "consulta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConsulta;

    private String sintomas;
    private String diagnostico;
    private LocalDate fecha;

    private Integer idCita;
    private Integer idPaciente;
    private Integer idMedico;

}

