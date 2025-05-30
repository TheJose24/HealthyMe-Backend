package dev.juliancamacho.healthyme_personal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

// Tabla
@Table(name = "horario_medico")
public class HorarioMedico extends Auditable {

    @EmbeddedId
    private HorarioMedicoId id;

    @ManyToOne
    @MapsId("idMedico")
    @JoinColumn(name = "id_medico")
    private Medico medico;

    @ManyToOne
    @MapsId("idHorario")
    @JoinColumn(name = "id_horario", columnDefinition = "INT")
    private HorarioTrabajo horario;

    @Embeddable
    public class HorarioMedicoId implements Serializable {
        private Integer idMedico;
        private Integer idHorario;
    }
}