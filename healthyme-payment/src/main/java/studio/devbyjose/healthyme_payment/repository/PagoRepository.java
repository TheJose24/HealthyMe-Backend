package studio.devbyjose.healthyme_payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;
import studio.devbyjose.healthyme_payment.dto.IngresosPorDiaDTO;
import studio.devbyjose.healthyme_payment.entity.Pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    Optional<Pago> findByEntidadReferencia(EntidadOrigen entidadReferencia);
    List<Pago> findByIdPaciente(Long idPaciente);
    List<Pago> findByEstado(EstadoPago estado);
    Optional<Pago> findByPaymentIntentId(String paymentIntentId);
    Optional<Pago> findByEntidadReferenciaAndEntidadReferenciaId(EntidadOrigen entidadReferencia, Integer idReferencia);
    List<Pago> findTop3ByOrderByFechaPagoDesc();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.estado = 'COMPLETADO'")
    BigDecimal sumTotalIngresos();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.estado = 'COMPLETADO' AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumIngresosPorPeriodo(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    Long countPagosPorPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                              @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT new studio.devbyjose.healthyme_payment.dto.IngresosPorDiaDTO(DATE(p.fechaPago), COALESCE(SUM(p.monto), 0)) " +
            "FROM Pago p WHERE p.estado = 'COMPLETADO' AND DATE(p.fechaPago) BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY DATE(p.fechaPago) ORDER BY DATE(p.fechaPago)")
    List<IngresosPorDiaDTO> getIngresosPorDiaEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                                     @Param("fechaFin") LocalDate fechaFin);
}