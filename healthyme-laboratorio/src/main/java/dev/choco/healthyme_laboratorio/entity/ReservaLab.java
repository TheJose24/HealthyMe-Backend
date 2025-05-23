package dev.choco.healthyme_laboratorio.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.choco.healthyme_laboratorio.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.*;

@Entity
@Table(name = "reserva_lab")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaLab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_lab")
    private Integer idReservaLab;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "id_tecnico", nullable = false)
    private Integer idTecnico;

    @Column(name = "id_laboratorio", nullable = false)
    private Integer idLaboratorio;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;
}
