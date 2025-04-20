package studio.devbyjose.healthyme_notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_notification.enums.TipoPlantilla;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "plantilla")
public class Plantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla")
    private Integer idPlantilla;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoPlantilla tipo;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "asunto", length = 200)
    private String asunto;

    @Column(name = "variables", nullable = false, columnDefinition = "JSON")
    private String variables;
}
