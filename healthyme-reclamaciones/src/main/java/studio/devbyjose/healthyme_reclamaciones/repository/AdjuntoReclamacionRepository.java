package studio.devbyjose.healthyme_reclamaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_reclamaciones.entity.AdjuntoReclamacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdjuntoReclamacionRepository extends JpaRepository<AdjuntoReclamacion, Long> {
    
    // Buscar adjuntos por reclamación
    List<AdjuntoReclamacion> findByReclamacionIdOrderByFechaSubidaDesc(Long reclamacionId);
    
    List<AdjuntoReclamacion> findByReclamacionId(Long reclamacionId);
    
    // Buscar por nombre de archivo
    Optional<AdjuntoReclamacion> findByNombreArchivo(String nombreArchivo);
    
    List<AdjuntoReclamacion> findByNombreArchivoContainingIgnoreCase(String nombreArchivo);
    
    // Buscar evidencias
    List<AdjuntoReclamacion> findByEsEvidenciaTrue();
    
    List<AdjuntoReclamacion> findByReclamacionIdAndEsEvidenciaTrue(Long reclamacionId);
    
    // Buscar por tipo de contenido
    List<AdjuntoReclamacion> findByTipoContenido(String tipoContenido);
    
    List<AdjuntoReclamacion> findByTipoContenidoStartingWith(String prefijo);
    
    // Buscar por usuario que subió
    List<AdjuntoReclamacion> findBySubidoPor(String subidoPor);
    
    List<AdjuntoReclamacion> findBySubidoPorOrderByFechaSubidaDesc(String subidoPor);
    
    // Buscar por fecha
    List<AdjuntoReclamacion> findByFechaSubidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Buscar por tamaño
    List<AdjuntoReclamacion> findByTamanoArchivoGreaterThan(Long tamano);
    
    List<AdjuntoReclamacion> findByTamanoArchivoLessThan(Long tamano);
    
    // Estadísticas
    @Query("SELECT COUNT(a) FROM AdjuntoReclamacion a WHERE a.reclamacion.id = :reclamacionId")
    Long countByReclamacionId(@Param("reclamacionId") Long reclamacionId);
    
    @Query("SELECT SUM(a.tamanoArchivo) FROM AdjuntoReclamacion a WHERE a.reclamacion.id = :reclamacionId")
    Long getTamanoTotalByReclamacionId(@Param("reclamacionId") Long reclamacionId);
    
    @Query("SELECT a.tipoContenido, COUNT(a) FROM AdjuntoReclamacion a " +
           "GROUP BY a.tipoContenido ORDER BY COUNT(a) DESC")
    List<Object[]> getEstadisticasTipoContenido();
    
    // Verificar si existe archivo
    boolean existsByNombreArchivo(String nombreArchivo);
    
    boolean existsByReclamacionId(Long reclamacionId);
    
    // Archivos por fecha de subida
    @Query("SELECT a FROM AdjuntoReclamacion a WHERE " +
           "a.fechaSubida >= :fechaInicio AND a.fechaSubida <= :fechaFin " +
           "ORDER BY a.fechaSubida DESC")
    List<AdjuntoReclamacion> findByRangoFechas(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin);
}
