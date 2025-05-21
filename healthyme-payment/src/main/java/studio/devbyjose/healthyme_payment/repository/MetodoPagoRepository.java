package studio.devbyjose.healthyme_payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_commons.enums.payment.TipoMetodoPago;
import studio.devbyjose.healthyme_payment.entity.MetodoPago;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
    List<MetodoPago> findByEstadoTrue();
    Optional<MetodoPago> findByTipoAndNombre(TipoMetodoPago tipo, String nombre);
}