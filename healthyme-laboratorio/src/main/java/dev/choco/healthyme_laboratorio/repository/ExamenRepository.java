package dev.choco.healthyme_laboratorio.repository;

import dev.choco.healthyme_laboratorio.entity.Examen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamenRepository extends JpaRepository<Examen, Integer> {
}
