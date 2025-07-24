package dev.choco.healthyme_laboratorio.repository;

import dev.choco.healthyme_laboratorio.entity.Examen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamenRepository extends JpaRepository<Examen, Integer> {
    List<Examen> findByIdPacienteOrderByFechaRealizacionDesc(Integer idPaciente);
}
