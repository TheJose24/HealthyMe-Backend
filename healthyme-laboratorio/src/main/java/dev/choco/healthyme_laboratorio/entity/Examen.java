package dev.choco.healthyme_laboratorio.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.*;

@Entity
@Table(name = "examenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen")
    private Integer idExamen;

    @Column(name = "nombre_examen", nullable = false)
    private String nombreExamen;

    @Column(name = "resultados")
    private String resultados;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fecha_realizacion")
    private LocalDate fechaRealizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva_lab", nullable = false)
    private ReservaLab reservaLab;

    @Column(name = "id_laboratorio", nullable = false)
    private Integer idLaboratorio;

    @Column(name = "id_tecnico", nullable = false)
    private Integer idTecnico;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;
}
