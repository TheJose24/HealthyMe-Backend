package dev.diegoqm.healthyme_infraestructura.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consultorio")
public class Consultorio extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consultorio")
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "piso", nullable = false)
    private Integer piso;

    @Column(name = "numero_habitacion", nullable = false)
    private Integer numeroHabitacion;

    @ManyToOne
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @Column(name = "id_especialidad", nullable = false)
    private Integer idEspecialidad;

}