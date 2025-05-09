package studio.devbyjose.security_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, length = 20)
    private String nombreRol;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @JsonBackReference
    @OneToMany(mappedBy = "rol", cascade = CascadeType.PERSIST)
    private List<Usuario> usuarios = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;

    // Constantes para los roles principales
    public static final String ROLE_DEFAULT = "USER";
    public static final String ROLE_PACIENTE = "PACIENTE";
    public static final String ROLE_MEDICO = "MEDICO";
    public static final String ROLE_ENFERMERO = "ENFERMERO";
    public static final String ROLE_ADMIN = "ADMIN";

    // Metodo de ayuda para la relación con Usuario
    public void addUsuario(Usuario usuario) {
        usuarios.add(usuario);
        usuario.setRol(this);
    }
}