package dev.juliancamacho.healthyme_personal.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

// Tabla
@Table(name = "unidad")
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private int idUnidad;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombreUnidad;


    @Column(name = "img_unidad", length = 50)
    private String imgUnidad;
}
