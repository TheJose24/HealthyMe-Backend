package studio.devbyjose.healthyme_notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_notification.entity.Plantilla;
import studio.devbyjose.healthyme_notification.enums.TipoPlantilla;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaRepository extends JpaRepository<Plantilla, Integer> {

    List<Plantilla> findByTipo(TipoPlantilla tipo);

    Optional<Plantilla> findByNombre(String nombre);

    List<Plantilla> findByNombreContainingIgnoreCase(String nombre);
}