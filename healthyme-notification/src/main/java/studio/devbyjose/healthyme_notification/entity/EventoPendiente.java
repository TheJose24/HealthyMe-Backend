package studio.devbyjose.healthyme_notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_pendientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoPendiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // "CITA", "RECETA", "EXAMEN"

    @Column(nullable = false)
    private String tipoEvento; // "CREACION", "ACTUALIZACION", etc.

    @Column(nullable = false)
    private Integer idEntidad; // ID de la cita, receta, etc.

    @Column(nullable = false, length = 100)
    private String destinatario; // Email del destinatario

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaProximoIntento;

    @Column(nullable = false)
    private Integer intentos;

    @Column(nullable = false, length = 2000)
    private String datosEvento; // JSON con los datos completos del evento

    @Column(length = 500)
    private String mensajeError;

    @Column(nullable = false)
    private Boolean procesado;
}