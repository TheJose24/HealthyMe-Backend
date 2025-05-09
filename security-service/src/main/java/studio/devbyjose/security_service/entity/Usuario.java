package studio.devbyjose.security_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import studio.devbyjose.security_service.enums.EstadoUsuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario", nullable = false, length = 50, unique = true)
    private String nombreUsuario;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(name = "imagen_perfil", length = 100, columnDefinition = "VARCHAR(255) DEFAULT 'default-profile.png'")
    private String imagenPerfil;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dni", nullable = false)
    private Persona persona;

    @JsonManagedReference
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Contrato> contratos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "intentos_fallidos", columnDefinition = "INTEGER DEFAULT 0")
    private Integer intentosFallidos = 0;

    // Métodos de ayuda para la relación con Contrato
    public void addContrato(Contrato contrato) {
        contratos.add(contrato);
        contrato.setUsuario(this);
    }

    public void removeContrato(Contrato contrato) {
        contratos.remove(contrato);
        contrato.setUsuario(null);
    }

    // Métodos de ayuda para la seguridad
    public void registrarLoginExitoso() {
        this.ultimoLogin = LocalDateTime.now();
        this.intentosFallidos = 0;
    }

    public void registrarIntentoFallido() {
        this.intentosFallidos++;
    }

    public Contrato getContratoActivo() {
        if (contratos == null || contratos.isEmpty()) {
            return null;
        }

        return contratos.stream()
                .filter(c -> c.getFechaFin().isAfter(LocalDateTime.now().toLocalDate()) &&
                        c.getFechaInicio().isBefore(LocalDateTime.now().plusDays(1).toLocalDate()))
                .findFirst()
                .orElse(null);
    }

}