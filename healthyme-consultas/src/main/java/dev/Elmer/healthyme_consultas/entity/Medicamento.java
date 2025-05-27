package dev.Elmer.healthyme_consultas.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Medicamento {

    private String nombre;

    private String dosis;

    private String indicaciones;
}