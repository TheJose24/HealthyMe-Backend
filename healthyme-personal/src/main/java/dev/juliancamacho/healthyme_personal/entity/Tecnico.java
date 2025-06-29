package dev.juliancamacho.healthyme_personal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

// Tabla
@Table(name = "tecnico")
public class Tecnico extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tecnico")
    private Integer idTecnico;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_unidad", nullable = false)
    private Unidad unidad;

    @OneToMany(mappedBy = "tecnico")
    private Set<HorarioTecnico> horariosTecnico;
}
