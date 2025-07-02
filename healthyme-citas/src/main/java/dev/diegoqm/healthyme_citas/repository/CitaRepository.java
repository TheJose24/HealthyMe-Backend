package dev.diegoqm.healthyme_citas.repository;

import dev.diegoqm.healthyme_citas.entity.Cita;
import dev.diegoqm.healthyme_citas.enums.EstadoCita;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface CitaRepository extends MongoRepository<Cita, String> {
    List<Cita> findByFecha(LocalDate fecha);

    List<Cita> findByIdPacienteOrderByFechaDesc(Long idPaciente);
    List<Cita> findByIdPacienteAndEstadoOrderByFechaDesc(Long idPaciente, EstadoCita estado);

    Optional<Cita> findFirstByIdPacienteAndFechaAfterOrderByFechaAscHoraAsc(Long idPaciente, LocalDate hoy);

    Long countByIdPacienteAndEstado(Long idPaciente, EstadoCita estado);
    List<Cita> findByIdPaciente(Long idPaciente, Pageable pageable);
}
