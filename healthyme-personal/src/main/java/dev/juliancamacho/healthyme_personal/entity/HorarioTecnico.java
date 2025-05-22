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
@Table(name = "horario_tecnico")
public class HorarioTecnico extends Auditable {

    @EmbeddedId
    private HorarioTecnicoId id;

    @ManyToOne
    @MapsId("idTecnico")
    @JoinColumn(name = "id_tecnico")
    private Tecnico tecnico;

    @ManyToOne
    @MapsId("idHorario")
    @JoinColumn(name = "id_horario")
    private HorarioTrabajo horario;

    @Embeddable
    public class HorarioTecnicoId implements Serializable {
        private Long idTecnico;
        private Long idHorario;
    }
}
