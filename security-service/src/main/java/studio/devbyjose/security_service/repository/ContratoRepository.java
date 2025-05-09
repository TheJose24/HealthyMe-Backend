package studio.devbyjose.security_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import studio.devbyjose.security_service.entity.Contrato;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    @Query("SELECT c FROM Contrato c WHERE c.usuario.idUsuario = :userId")
    List<Contrato> findByUsuarioId(Integer userId);

    @Query("SELECT c FROM Contrato c WHERE c.fechaInicio <= :hoy AND c.fechaFin >= :hoy")
    List<Contrato> findActiveContracts(LocalDate hoy);

    @Query("SELECT COUNT(c) FROM Contrato c WHERE c.fechaFin < :hoy")
    long countExpiredContracts(LocalDate hoy);
}