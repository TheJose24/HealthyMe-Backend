package dev.diegoqm.healthyme_infraestructura.repository;

import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultorioRepository extends JpaRepository<Consultorio, Integer> {
}
