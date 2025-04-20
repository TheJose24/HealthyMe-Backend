package studio.devbyjose.healthyme_notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "configuracion_notificacion")
public class ConfiguracionNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Integer idConfiguracion;

    @Column(name = "id_usuario", nullable = false, unique = true)
    private Integer idUsuario;

    @Builder.Default
    @Column(name = "recibir_email", nullable = false)
    private Boolean recibirEmail = true;

    @Builder.Default
    @Column(name = "recibir_sms", nullable = false)
    private Boolean recibirSms = false;

    @Builder.Default
    @Column(name = "recibir_push", nullable = false)
    private Boolean recibirPush = true;
}

