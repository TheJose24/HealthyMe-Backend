package dev.diegoqm.healthyme_infraestructura.repository;

import dev.diegoqm.healthyme_infraestructura.entity.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> {
}