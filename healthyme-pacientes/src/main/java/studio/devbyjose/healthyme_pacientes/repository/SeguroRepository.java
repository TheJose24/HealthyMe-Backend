package studio.devbyjose.healthyme_pacientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.devbyjose.healthyme_pacientes.entity.Seguro;

public interface SeguroRepository extends JpaRepository<Seguro, Long> {
}