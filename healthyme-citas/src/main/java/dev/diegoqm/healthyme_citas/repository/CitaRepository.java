package dev.diegoqm.healthyme_citas.repository;

import dev.diegoqm.healthyme_citas.entity.Cita;
import dev.diegoqm.healthyme_citas.enums.EstadoCita;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorDiaDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface CitaRepository extends JpaRepository<Cita, String> {


    List<Cita> findByFecha(LocalDate fecha);

    List<Cita> findByIdPacienteOrderByFechaDesc(Long idPaciente);

    List<Cita> findByIdPacienteAndEstadoOrderByFechaDesc(Long idPaciente, EstadoCita estado);

    // Próxima cita del paciente
    @Query("SELECT c FROM Cita c WHERE c.idPaciente = :idPaciente AND c.fecha > :fechaActual " +
            "ORDER BY c.fecha ASC, c.hora ASC")
    Optional<Cita> findFirstByIdPacienteAndFechaAfterOrderByFechaAscHoraAsc(
            @Param("idPaciente") Long idPaciente,
            @Param("fechaActual") LocalDate fechaActual);

    Long countByIdPacienteAndEstado(Long idPaciente, EstadoCita estado);

    List<Cita> findByIdPaciente(Long idPaciente, Pageable pageable);

    // CITA POR ESTADO
    List<Cita> findByEstado(EstadoCita estado);

    // Citas en un rango de fechas
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long countByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio,
                             @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado AND c.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long countByEstadoAndFechaBetween(@Param("estado") EstadoCita estado,
                                      @Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT c.fecha, COUNT(c) " +
            "FROM Cita c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY c.fecha ORDER BY c.fecha")
    List<Object[]> findCitasPorDiaEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                          @Param("fechaFin") LocalDate fechaFin);

    List<Cita> findByIdMedicoAndFecha(
            Integer idMedico,
            LocalDate fecha);

    List<Cita> findByIdMedicoAndFechaAndEstado(
            Integer idMedico,
            LocalDate fecha,
            EstadoCita estado);

    List<Cita> findByIdMedicoAndFechaBetween(
            Integer idMedico,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

}