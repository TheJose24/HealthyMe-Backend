package dev.diegoqm.healthyme_citas.repository;

import dev.diegoqm.healthyme_citas.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
}
