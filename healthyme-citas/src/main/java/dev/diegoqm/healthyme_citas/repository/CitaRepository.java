package dev.diegoqm.healthyme_citas.repository;

import dev.diegoqm.healthyme_citas.entity.Cita;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;


public interface CitaRepository extends MongoRepository<Cita, String> {
    List<Cita> findByFecha(LocalDate fecha);
}
