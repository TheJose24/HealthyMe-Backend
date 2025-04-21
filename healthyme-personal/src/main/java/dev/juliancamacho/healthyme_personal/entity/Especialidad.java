package dev.juliancamacho.healthyme_personal.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

// Tabla
@Table(name = "especialidad")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private int idEspecialidad;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombreEspecialidad;

    @Column(name = "img_especialidad", length = 50)
    private String imgEspecialidad;
}
