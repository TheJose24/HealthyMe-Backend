package studio.devbyjose.healthyme_payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.entity.Transaccion;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    List<Transaccion> findByPago(Pago pago);
    Optional<Transaccion> findByReferenciaExterna(String referenciaExterna);
}