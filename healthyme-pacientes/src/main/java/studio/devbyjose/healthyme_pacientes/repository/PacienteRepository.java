package studio.devbyjose.healthyme_pacientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByIdUsuario(Long idUsuario);
    boolean existsByIdUsuario(Long idUsuario);

    @Query("SELECT FUNCTION('MONTH', p.fechaCreacion), COUNT(p) FROM Paciente p GROUP BY FUNCTION('MONTH', p.fechaCreacion)")
    List<Object[]> countPacientesPorMes();

}