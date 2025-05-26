package dev.Elmer.healthyme_consultas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @ElementCollection
    @CollectionTable(name = "medicamento_receta", joinColumns = @JoinColumn(name = "id_receta"))
    private List<Medicamento> medicamentos;

    @ManyToOne
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;

}