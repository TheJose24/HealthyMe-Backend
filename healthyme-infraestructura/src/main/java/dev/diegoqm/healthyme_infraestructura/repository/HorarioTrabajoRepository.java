package dev.diegoqm.healthyme_infraestructura.repository;

import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioTrabajoRepository extends JpaRepository<HorarioTrabajo, Integer> {
}