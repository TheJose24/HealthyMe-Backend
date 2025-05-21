package studio.devbyjose.healthyme_notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_notification.entity.Notificacion;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.notification.EstadoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    Page<Notificacion> findByDestinatario(String destinatario, Pageable pageable);

    List<Notificacion> findByEstado(EstadoNotificacion estado);

    List<Notificacion> findByEntidadOrigenAndIdOrigen(EntidadOrigen entidadOrigen, Integer idOrigen);

    List<Notificacion> findByEstadoAndFechaEnvioBefore(EstadoNotificacion estado, LocalDateTime fecha);
}