package studio.devbyjose.healthyme_notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_notification.entity.Adjunto;

import java.util.List;

@Repository
public interface AdjuntoRepository extends JpaRepository<Adjunto, Integer> {

    List<Adjunto> findByNotificacionIdNotificacion(Integer idNotificacion);

    void deleteByNotificacionIdNotificacion(Integer idNotificacion);
}