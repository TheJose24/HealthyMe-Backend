package studio.devbyjose.healthyme_pacientes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "triaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Triaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_triaje")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(name = "peso", nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(name = "talla", nullable = false, precision = 5, scale = 2)
    private BigDecimal talla;

    @Column(name = "alergias", length = 100, nullable = false)
    private String alergias;

    @Column(name = "condiciones_medicas", length = 100, nullable = false)
    private String condicionesMedicas;

    @Column(name = "antecedentes_familiares", length = 100, nullable = false)
    private String antecedentesFamiliares;

    @Column(name = "presion_arterial", nullable = false, precision = 5, scale = 2)
    private BigDecimal presionArterial;

    @Column(name = "frecuencia_cardiaca", nullable = false, precision = 5, scale = 2)
    private BigDecimal frecuenciaCardiaca;

    @Column(name = "frecuencia_respiratoria", nullable = false, precision = 5, scale = 2)
    private BigDecimal frecuenciaRespiratoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;
}