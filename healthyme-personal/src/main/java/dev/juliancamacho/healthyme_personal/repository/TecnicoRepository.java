package dev.juliancamacho.healthyme_personal.repository;

import dev.juliancamacho.healthyme_personal.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TecnicoRepository extends JpaRepository<Tecnico, Integer>
{
}
