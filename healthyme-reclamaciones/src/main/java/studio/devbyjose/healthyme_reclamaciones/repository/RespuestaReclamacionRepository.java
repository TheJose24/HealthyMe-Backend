package studio.devbyjose.healthyme_reclamaciones.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_reclamaciones.entity.RespuestaReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoRespuesta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaReclamacionRepository extends JpaRepository<RespuestaReclamacion, Long> {
    
    // =================== CONSULTAS BÁSICAS POR RECLAMACIÓN ===================
    
    /**
     * Buscar respuestas por reclamación ordenadas por fecha
     */
    List<RespuestaReclamacion> findByReclamacionIdOrderByFechaRespuesta(Long reclamacionId);
    
    /**
     * Buscar respuestas paginadas por reclamación
     */
    Page<RespuestaReclamacion> findByReclamacionIdOrderByFechaRespuesta(Long reclamacionId, Pageable pageable);
    
    /**
     * Buscar respuestas por reclamación (sin orden específico)
     */
    List<RespuestaReclamacion> findByReclamacionId(Long reclamacionId);
    
    // =================== PRIMERA Y ÚLTIMA RESPUESTA ===================
    
    /**
     * Obtener primera respuesta por fecha
     */
    Optional<RespuestaReclamacion> findFirstByReclamacionIdOrderByFechaRespuesta(Long reclamacionId);
    
    /**
     * Obtener última respuesta por fecha
     */
    Optional<RespuestaReclamacion> findFirstByReclamacionIdOrderByFechaRespuestaDesc(Long reclamacionId);
    
    // =================== CONSULTAS POR TIPO ===================
    
    /**
     * Buscar por reclamación y tipo de respuesta
     */
    List<RespuestaReclamacion> findByReclamacionIdAndTipoRespuestaOrderByFechaRespuesta(Long reclamacionId, TipoRespuesta tipoRespuesta);
    
    /**
     * Buscar una respuesta específica por reclamación y tipo
     */
    Optional<RespuestaReclamacion> findByReclamacionIdAndTipoRespuesta(Long reclamacionId, TipoRespuesta tipoRespuesta);
    
    /**
     * Buscar todas las respuestas de un tipo
     */
    List<RespuestaReclamacion> findByTipoRespuesta(TipoRespuesta tipoRespuesta);
    
    // =================== CONSULTAS POR RESPONSABLE (quien responde) ===================
    
    /**
     * Buscar respuestas por usuario responsable (paginado)
     */
    Page<RespuestaReclamacion> findByResponsableOrderByFechaRespuestaDesc(String responsable, Pageable pageable);
    
    /**
     * Buscar respuestas por usuario responsable (lista)
     */
    List<RespuestaReclamacion> findByResponsable(String responsable);
    
    // =================== CONSULTAS POR ÁREA (a través de la relación con Reclamacion) ===================
    
    /**
     * Buscar respuestas por área responsable a través de la reclamación (paginado)
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE r.reclamacion.areaResponsable = :areaResponsable ORDER BY r.fechaRespuesta DESC")
    Page<RespuestaReclamacion> findByReclamacionAreaResponsableOrderByFechaRespuestaDesc(@Param("areaResponsable") String areaResponsable, Pageable pageable);
    
    /**
     * Buscar respuestas por área responsable a través de la reclamación (lista)
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE r.reclamacion.areaResponsable = :areaResponsable")
    List<RespuestaReclamacion> findByReclamacionAreaResponsable(@Param("areaResponsable") String areaResponsable);
    
    // =================== BÚSQUEDA POR CONTENIDO ===================
    
    /**
     * Buscar por contenido de respuesta (paginado)
     */
    Page<RespuestaReclamacion> findByContenidoContainingIgnoreCase(String contenido, Pageable pageable);
    
    // =================== CONSULTAS POR FECHAS ===================
    
    /**
     * Buscar respuestas por rango de fechas (paginado)
     */
    Page<RespuestaReclamacion> findByFechaRespuestaBetweenOrderByFechaRespuestaDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
    
    /**
     * Buscar respuestas por rango de fechas (lista)
     */
    List<RespuestaReclamacion> findByFechaRespuestaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // =================== CONSULTAS DE NOTIFICACIÓN ===================
    
    /**
     * Buscar por estado de notificación
     */
    List<RespuestaReclamacion> findByNotificadoCliente(Boolean notificado);
    
    /**
     * Respuestas no notificadas al cliente (paginado)
     */
    Page<RespuestaReclamacion> findByNotificadoClienteFalse(Pageable pageable);
    
    /**
     * Respuestas pendientes de notificación con email válido
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE " +
           "r.notificadoCliente = false AND r.reclamacion.emailReclamante IS NOT NULL")
    List<RespuestaReclamacion> findRespuestasPendientesNotificacion();
    
    // =================== CONSULTAS POR RESPUESTA FINAL ===================
    
    /**
     * Buscar respuesta final de una reclamación
     */
    Optional<RespuestaReclamacion> findByReclamacionIdAndEsRespuestaFinalTrue(Long reclamacionId);
    
    /**
     * Buscar todas las respuestas finales no notificadas
     */
    List<RespuestaReclamacion> findByEsRespuestaFinalTrueAndNotificadoClienteFalse();
    
    // =================== VERIFICACIONES DE EXISTENCIA ===================
    
    /**
     * Verificar si existe respuesta para una reclamación
     */
    boolean existsByReclamacionId(Long reclamacionId);
    
    /**
     * Verificar si existe respuesta de un tipo específico para una reclamación
     */
    boolean existsByReclamacionIdAndTipoRespuesta(Long reclamacionId, TipoRespuesta tipoRespuesta);
    
    /**
     * Verificar si existe respuesta final para una reclamación
     */
    boolean existsByReclamacionIdAndEsRespuestaFinalTrue(Long reclamacionId);
    
    // =================== CONTADORES BÁSICOS ===================
    
    /**
     * Contar respuestas por reclamación
     */
    long countByReclamacionId(Long reclamacionId);
    
    /**
     * Contar respuestas por tipo
     */
    long countByTipoRespuesta(TipoRespuesta tipoRespuesta);
    
    /**
     * Contar respuestas por responsable
     */
    long countByResponsable(String responsable);
    
    /**
     * Contar respuestas por área responsable (a través de la reclamación)
     */
    @Query("SELECT COUNT(r) FROM RespuestaReclamacion r WHERE r.reclamacion.areaResponsable = :areaResponsable")
    long countByReclamacionAreaResponsable(@Param("areaResponsable") String areaResponsable);
    
    // =================== CONTADORES ESPECÍFICOS ===================
    
    /**
     * Contar respuestas no notificadas
     */
    long countByNotificadoClienteFalse();
    
    /**
     * Contar respuestas finales no notificadas
     */
    long countByEsRespuestaFinalTrueAndNotificadoClienteFalse();
    
    // =================== CONSULTAS ESTADÍSTICAS Y REPORTES ===================
    
    /**
     * Contar respuestas por responsable y rango de fechas
     */
    @Query("SELECT COUNT(r) FROM RespuestaReclamacion r WHERE " +
           "r.responsable = :responsable AND r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin")
    long countByResponsableAndFechaRespuestaBetween(
            @Param("responsable") String responsable,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Contar respuestas por área y rango de fechas
     */
    @Query("SELECT COUNT(r) FROM RespuestaReclamacion r WHERE " +
           "r.reclamacion.areaResponsable = :areaResponsable AND r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin")
    long countByAreaResponsableAndFechaRespuestaBetween(
            @Param("areaResponsable") String areaResponsable,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
    
    // =================== CONSULTAS DE TIEMPO CORREGIDAS ===================
    
    /**
     * Obtener la primera respuesta para calcular tiempo manualmente
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE r.reclamacion.id = :reclamacionId " +
           "ORDER BY r.fechaRespuesta ASC")
    List<RespuestaReclamacion> findPrimeraRespuestaByReclamacionId(@Param("reclamacionId") Long reclamacionId);
    
    /**
     * Obtener la respuesta final para calcular tiempo manualmente
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE r.reclamacion.id = :reclamacionId " +
           "AND r.esRespuestaFinal = true")
    Optional<RespuestaReclamacion> findRespuestaFinalByReclamacionId(@Param("reclamacionId") Long reclamacionId);
    
    /**
     * Top responsables por cantidad de respuestas en un período
     */
    @Query("SELECT r.responsable, COUNT(r) FROM RespuestaReclamacion r " +
           "WHERE r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY r.responsable ORDER BY COUNT(r) DESC")
    List<Object[]> findTopResponsablesByRespuestas(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                  @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Top áreas por cantidad de respuestas en un período
     */
    @Query("SELECT r.reclamacion.areaResponsable, COUNT(r) FROM RespuestaReclamacion r " +
           "WHERE r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY r.reclamacion.areaResponsable ORDER BY COUNT(r) DESC")
    List<Object[]> findTopAreasByRespuestas(@Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Distribución de respuestas por tipo en un período
     */
    @Query("SELECT r.tipoRespuesta, COUNT(r) FROM RespuestaReclamacion r " +
           "WHERE r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY r.tipoRespuesta ORDER BY COUNT(r) DESC")
    List<Object[]> findDistribucionPorTipoRespuesta(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                   @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Búsqueda avanzada con múltiples filtros
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE " +
           "(:responsable IS NULL OR r.responsable = :responsable) AND " +
           "(:tipoRespuesta IS NULL OR r.tipoRespuesta = :tipoRespuesta) AND " +
           "(:fechaInicio IS NULL OR r.fechaRespuesta >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR r.fechaRespuesta <= :fechaFin) AND " +
           "(:areaResponsable IS NULL OR r.reclamacion.areaResponsable = :areaResponsable)")
    Page<RespuestaReclamacion> findWithFilters(@Param("responsable") String responsable,
                                              @Param("tipoRespuesta") TipoRespuesta tipoRespuesta,
                                              @Param("fechaInicio") LocalDateTime fechaInicio,
                                              @Param("fechaFin") LocalDateTime fechaFin,
                                              @Param("areaResponsable") String areaResponsable,
                                              Pageable pageable);
    
    /**
     * Últimas respuestas para dashboard - CORREGIDO: Usar Pageable en lugar de LIMIT
     */
    @Query("SELECT r FROM RespuestaReclamacion r ORDER BY r.fechaRespuesta DESC")
    Page<RespuestaReclamacion> findUltimasRespuestas(Pageable pageable);
    
    /**
     * Respuestas que requieren seguimiento
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE r.tipoRespuesta = :tipoSolicitud " +
           "AND r.fechaRespuesta < :fechaLimite AND NOT EXISTS " +
           "(SELECT r2 FROM RespuestaReclamacion r2 WHERE r2.reclamacion.id = r.reclamacion.id " +
           "AND r2.fechaRespuesta > r.fechaRespuesta)")
    List<RespuestaReclamacion> findRespuestasQueRequierenSeguimiento(@Param("tipoSolicitud") TipoRespuesta tipoSolicitud,
                                                                    @Param("fechaLimite") LocalDateTime fechaLimite);
    
    // =================== CONSULTAS SIMPLIFICADAS PARA REPORTES ===================
    
    /**
     * Respuestas por área en un período (simplificado)
     */
    @Query("SELECT r.reclamacion.areaResponsable, COUNT(r) FROM RespuestaReclamacion r " +
           "WHERE r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY r.reclamacion.areaResponsable")
    List<Object[]> countRespuestasPorArea(@Param("fechaInicio") LocalDateTime fechaInicio,
                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Respuestas por responsable en un período (simplificado)
     */
    @Query("SELECT r.responsable, COUNT(r) FROM RespuestaReclamacion r " +
           "WHERE r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY r.responsable")
    List<Object[]> countRespuestasPorResponsable(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Todas las respuestas para calcular tiempos en el servicio
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE r.fechaRespuesta BETWEEN :fechaInicio AND :fechaFin")
    List<RespuestaReclamacion> findAllRespuestasByPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Respuestas finales por área
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE r.esRespuestaFinal = true " +
           "AND r.reclamacion.areaResponsable = :areaResponsable")
    List<RespuestaReclamacion> findRespuestasFinalesByArea(@Param("areaResponsable") String areaResponsable);
    
    /**
     * Respuestas por fecha específica
     */
    @Query("SELECT r FROM RespuestaReclamacion r WHERE DATE(r.fechaRespuesta) = DATE(:fecha)")
    List<RespuestaReclamacion> findByFechaRespuesta(@Param("fecha") LocalDateTime fecha);
}
