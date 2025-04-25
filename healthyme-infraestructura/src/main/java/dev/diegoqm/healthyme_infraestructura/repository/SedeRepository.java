package dev.diegoqm.healthyme_infraestructura.repository;

import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {
}