package studio.devbyjose.security_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.security_service.entity.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndDniNot(String email, String dni);
}