package studio.devbyjose.healthyme_payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.Pago;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    Optional<Factura> findByPago(Pago pago);
}