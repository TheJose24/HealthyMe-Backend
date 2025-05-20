package dev.juliancamacho.healthyme_personal.repository;

import dev.juliancamacho.healthyme_personal.entity.Enfermero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnfermeroRepository extends JpaRepository<Enfermero, Integer>
{
}
