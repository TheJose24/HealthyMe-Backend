package studio.devbyjose.security_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import studio.devbyjose.security_service.entity.Usuario;
import studio.devbyjose.security_service.enums.EstadoUsuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByPersonaDni(String dni);

    long countByEstado(EstadoUsuario estado);

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombreRol = :rolNombre AND u.estado = :estado")
    List<Usuario> findByRolNombreAndEstado(String rolNombre, EstadoUsuario estado);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombreRol = :rolNombre")
    long countByRolNombre(String rolNombre);

    Page<Usuario> findAll(Specification<Usuario> spec, Pageable pageable);
}