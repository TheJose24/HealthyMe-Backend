package studio.devbyjose.healthyme_payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;
import studio.devbyjose.healthyme_payment.entity.Pago;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    Optional<Pago> findByEntidadReferencia(EntidadOrigen entidadReferencia);
    List<Pago> findByIdPaciente(Integer idPaciente);
    List<Pago> findByEstado(EstadoPago estado);
    Optional<Pago> findByPaymentIntentId(String paymentIntentId);
    Optional<Pago> findByEntidadReferenciaAndEntidadReferenciaId(EntidadOrigen entidadReferencia, Integer idReferencia);
}