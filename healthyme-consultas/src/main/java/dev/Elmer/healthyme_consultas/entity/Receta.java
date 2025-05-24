package dev.Elmer.healthyme_consultas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "receta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_receta")
    private Integer idReceta;

    @Column(name = "medicamento")
    private String medicamento;

    @Column(name = "dosis")
    private String dosis;

    @Column(name = "instrucciones")
    private Integer instrucciones;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @ManyToOne
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

}