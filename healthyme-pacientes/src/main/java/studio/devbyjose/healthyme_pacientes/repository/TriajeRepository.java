package studio.devbyjose.healthyme_pacientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.entity.Triaje;

import java.util.List;

public interface TriajeRepository extends JpaRepository<Triaje, Long> {
    List<Triaje> findByPacienteOrderByFechaDescHoraDesc(Paciente paciente);
}