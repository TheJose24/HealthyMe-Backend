package studio.devbyjose.healthyme_reclamaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_reclamaciones.entity.SeguimientoReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.EstadoReclamacion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeguimientoReclamacionRepository extends JpaRepository<SeguimientoReclamacion, Long> {
    
    // Buscar seguimientos por reclamación
    List<SeguimientoReclamacion> findByReclamacionIdOrderByFechaCambioDesc(Long reclamacionId);
    
    List<SeguimientoReclamacion> findByReclamacionId(Long reclamacionId);
    
    // Buscar seguimientos visibles al cliente
    List<SeguimientoReclamacion> findByReclamacionIdAndEsVisibleClienteTrueOrderByFechaCambioDesc(Long reclamacionId);
    
    // Buscar por usuario responsable
    List<SeguimientoReclamacion> findByUsuarioResponsable(String usuarioResponsable);
    
    List<SeguimientoReclamacion> findByUsuarioResponsableOrderByFechaCambioDesc(String usuarioResponsable);
    
    // Buscar por estados
    List<SeguimientoReclamacion> findByEstadoAnterior(EstadoReclamacion estadoAnterior);
    
    List<SeguimientoReclamacion> findByEstadoNuevo(EstadoReclamacion estadoNuevo);
    
    // Buscar cambios de estado específicos
    List<SeguimientoReclamacion> findByEstadoAnteriorAndEstadoNuevo(
        EstadoReclamacion estadoAnterior, 
        EstadoReclamacion estadoNuevo);
    
    // Buscar por fecha
    List<SeguimientoReclamacion> findByFechaCambioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Último seguimiento de una reclamación
    @Query("SELECT s FROM SeguimientoReclamacion s WHERE s.reclamacion.id = :reclamacionId " +
           "ORDER BY s.fechaCambio DESC")
    List<SeguimientoReclamacion> findLastSeguimientoByReclamacionId(@Param("reclamacionId") Long reclamacionId);
    
    // Estadísticas de cambios de estado
    @Query("SELECT s.estadoNuevo, COUNT(s) FROM SeguimientoReclamacion s " +
           "WHERE s.fechaCambio >= :fechaInicio AND s.fechaCambio <= :fechaFin " +
           "GROUP BY s.estadoNuevo")
    List<Object[]> getEstadisticasCambiosEstado(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin);
    
    // Tiempo promedio por estado
    @Query("SELECT s.estadoNuevo, AVG(DATEDIFF(s2.fechaCambio, s.fechaCambio)) " +
           "FROM SeguimientoReclamacion s " +
           "JOIN SeguimientoReclamacion s2 ON s.reclamacion.id = s2.reclamacion.id " +
           "WHERE s2.fechaCambio > s.fechaCambio " +
           "GROUP BY s.estadoNuevo")
    List<Object[]> getTiempoPromedioEstados();
    
    // Verificar si existe seguimiento para una reclamación
    boolean existsByReclamacionId(Long reclamacionId);
}
