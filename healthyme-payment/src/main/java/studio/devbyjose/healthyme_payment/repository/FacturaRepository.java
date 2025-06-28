package studio.devbyjose.healthyme_payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.Pago;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    Optional<Factura> findByPago(Pago pago);

    @Query("SELECT FUNCTION('MONTH', f.fechaEmision) AS mes, SUM(f.total) AS total " +
            "FROM Factura f GROUP BY FUNCTION('MONTH', f.fechaEmision)")
    List<Object[]> obtenerIngresosPorMes();

}