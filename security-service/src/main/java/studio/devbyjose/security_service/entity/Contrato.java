package studio.devbyjose.security_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrato")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Integer idContrato;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "salario")
    private BigDecimal salario;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "cargo", length = 100)
    private String cargo;

    @Column(name = "horas_semana")
    private Integer horasSemana;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;

    @Transient
    public boolean isActivo() {
        if (fechaInicio == null || fechaFin == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        return !today.isBefore(fechaInicio) && !today.isAfter(fechaFin);
    }

    @Transient
    public long getDuracionDias() {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }
}