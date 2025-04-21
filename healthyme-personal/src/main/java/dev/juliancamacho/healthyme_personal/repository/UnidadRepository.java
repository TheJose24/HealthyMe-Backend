package dev.juliancamacho.healthyme_personal.repository;

import dev.juliancamacho.healthyme_personal.entity.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadRepository  extends JpaRepository<Unidad, Integer> {
}
