package studio.devbyjose.healthyme_pacientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByIdUsuario(Long idUsuario);
    boolean existsByIdUsuario(Long idUsuario);
}