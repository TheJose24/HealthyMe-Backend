package dev.juliancamacho.healthyme_personal.repository;

import dev.juliancamacho.healthyme_personal.entity.HorarioMedico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Integer> {
}
