package dev.Elmer.healthyme_consultas.entity;

import dev.Elmer.healthyme_consultas.dto.MedicamentoDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private List<MedicamentoDto> medicamentos;

    @ManyToOne
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

}