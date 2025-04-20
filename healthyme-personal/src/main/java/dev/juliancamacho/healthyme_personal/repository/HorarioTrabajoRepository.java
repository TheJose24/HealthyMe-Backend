package dev.juliancamacho.healthyme_personal.repository;

import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioTrabajoRepository extends JpaRepository<HorarioTrabajo, Integer> {
}
