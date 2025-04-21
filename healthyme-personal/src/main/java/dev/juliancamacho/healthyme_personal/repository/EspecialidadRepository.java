package dev.juliancamacho.healthyme_personal.repository;

import dev.juliancamacho.healthyme_personal.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {
}
