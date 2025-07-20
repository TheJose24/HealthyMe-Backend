package dev.juliancamacho.healthyme_personal.repository;

import dev.juliancamacho.healthyme_personal.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    List<Medico> findByEspecialidadIdEspecialidad(Integer idEspecialidad);

    Long countByEstadoTrue(); // Contar médicos activos
}
