package dev.Elmer.healthyme_consultas.repository;

import dev.Elmer.healthyme_consultas.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {

}


