package studio.devbyjose.healthyme_notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_notification.entity.EventoPendiente;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoPendienteRepository extends JpaRepository<EventoPendiente, Long> {

    List<EventoPendiente> findByProcesadoFalseAndFechaProximoIntentoLessThan(LocalDateTime fecha);

    List<EventoPendiente> findByProcesadoFalseAndIntentosBetween(int intentosMin, int intentosMax);

    int deleteByProcesadoTrueAndFechaCreacionBefore(LocalDateTime fechaCreacion);

}