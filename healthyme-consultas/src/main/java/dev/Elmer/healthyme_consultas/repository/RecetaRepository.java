package dev.Elmer.healthyme_consultas.repository;

import dev.Elmer.healthyme_consultas.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {
    /** Todas las recetas emitidas al paciente (vía la consulta) */
    List<Receta> findByConsulta_IdPacienteOrderByFechaEmisionDesc(Integer idPaciente);
}


