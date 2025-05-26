package dev.diegoqm.healthyme_citas.repository;

import dev.diegoqm.healthyme_citas.entity.Cita;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CitaRepository extends MongoRepository<Cita, String> {
}
