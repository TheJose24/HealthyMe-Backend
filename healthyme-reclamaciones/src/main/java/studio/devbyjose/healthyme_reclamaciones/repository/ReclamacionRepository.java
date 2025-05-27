package studio.devbyjose.healthyme_reclamaciones.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_reclamaciones.entity.Reclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.EstadoReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.PrioridadReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoMotivo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamacionRepository extends JpaRepository<Reclamacion, Long>, JpaSpecificationExecutor<Reclamacion> {
    
    // Búsquedas básicas
    Optional<Reclamacion> findByNumeroReclamacion(String numeroReclamacion);
    boolean existsByNumeroReclamacion(String numeroReclamacion);
    
    // =================== CONSULTA PARA NUMERACIÓN ===================
    
    /**
     * Obtiene el último número de reclamación que coincida con un patrón específico
     * Se usa para la generación de secuencias de numeración
     */
    @Query("SELECT r.numeroReclamacion FROM Reclamacion r " +
           "WHERE r.numeroReclamacion LIKE :patron " +
           "ORDER BY r.numeroReclamacion DESC")
    Optional<String> findFirstByNumeroReclamacionLikeOrderByNumeroReclamacionDesc(@Param("patron") String patron);
    
    /**
     * Método de conveniencia para obtener el último número por patrón
     * Retorna null si no encuentra ninguno
     */
    default String findUltimoNumeroByPatron(String patron) {
        return findFirstByNumeroReclamacionLikeOrderByNumeroReclamacionDesc(patron).orElse(null);
    }
    
    /**
     * Obtiene el último número de hoja que coincida con un patrón específico
     * Para la numeración de hojas de reclamación
     */
    @Query("SELECT r.numeroHoja FROM Reclamacion r " +
           "WHERE r.numeroHoja LIKE :patron AND r.numeroHoja IS NOT NULL " +
           "ORDER BY r.numeroHoja DESC")
    Optional<String> findFirstByNumeroHojaLikeOrderByNumeroHojaDesc(@Param("patron") String patron);
    
    /**
     * Método de conveniencia para obtener el último número de hoja por patrón
     */
    default String findUltimaHojaByPatron(String patron) {
        return findFirstByNumeroHojaLikeOrderByNumeroHojaDesc(patron).orElse(null);
    }
    
    // =================== CONSULTAS EXISTENTES ===================
    
    // Consultas por estado
    Page<Reclamacion> findByEstadoNot(EstadoReclamacion estado, Pageable pageable);
    Page<Reclamacion> findByEstado(EstadoReclamacion estado, Pageable pageable);
    List<Reclamacion> findByEstado(EstadoReclamacion estado);
    long countByEstado(EstadoReclamacion estado);
    
    // Consultas por relaciones
    Page<Reclamacion> findByIdPacienteAndEstadoNot(Long idPaciente, EstadoReclamacion estado, Pageable pageable);
    List<Reclamacion> findByIdCitaAndEstadoNot(Long idCita, EstadoReclamacion estado);
    List<Reclamacion> findByIdPagoAndEstadoNot(Long idPago, EstadoReclamacion estado);
    
    // Búsqueda por texto
    Page<Reclamacion> findByDescripcionContainingIgnoreCaseOrDetalleIncidenteContainingIgnoreCaseAndEstadoNot(
        String descripcion, String detalle, EstadoReclamacion estado, Pageable pageable);
    
    // Consultas por asignación
    Page<Reclamacion> findByAsignadoAAndEstadoNotIn(String asignadoA, List<EstadoReclamacion> estados, Pageable pageable);
    Page<Reclamacion> findByAreaResponsableAndEstadoNotIn(String areaResponsable, List<EstadoReclamacion> estados, Pageable pageable);
    long countByAsignadoAAndEstadoNotIn(String asignadoA, List<EstadoReclamacion> estados);
    long countByAreaResponsableAndEstadoNotIn(String areaResponsable, List<EstadoReclamacion> estados);
    
    // Consultas por fechas
    Page<Reclamacion> findByEstadoNotInAndFechaLimiteRespuestaBefore(List<EstadoReclamacion> estados, LocalDateTime fecha, Pageable pageable);
    Page<Reclamacion> findByEstadoNotInAndFechaLimiteRespuestaBetween(List<EstadoReclamacion> estados, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    
    // Estadísticas
    long countByFechaReclamacionBetween(LocalDateTime inicio, LocalDateTime fin);
    long countByEstadoAndUpdatedAtBetween(EstadoReclamacion estado, LocalDateTime inicio, LocalDateTime fin);
    long countByEstadoNotIn(List<EstadoReclamacion> estados);
    long countByEstadoNotInAndFechaLimiteRespuestaBefore(List<EstadoReclamacion> estados, LocalDateTime fecha);
    long countByEstadoNotInAndFechaLimiteRespuestaBetween(List<EstadoReclamacion> estados, LocalDateTime inicio, LocalDateTime fin);
    long countByPrioridadAndEstadoNotIn(PrioridadReclamacion prioridad, List<EstadoReclamacion> estados);
    
    // Consultas para reclamaciones críticas
    List<Reclamacion> findByPrioridadAndEstadoNotInAndFechaLimiteRespuestaBefore(
        PrioridadReclamacion prioridad, 
        List<EstadoReclamacion> estados, 
        LocalDateTime fecha
    );
    
    // =================== CONSULTAS ADICIONALES PARA NUMERACIÓN ===================
    
    /**
     * Consulta optimizada para obtener la máxima secuencia de un año específico
     */
    @Query("SELECT MAX(CAST(SUBSTRING(r.numeroReclamacion, LENGTH(r.numeroReclamacion) - 5) AS long)) " +
           "FROM Reclamacion r " +
           "WHERE r.numeroReclamacion LIKE :patron")
    Optional<Long> findMaxSecuenciaByPatron(@Param("patron") String patron);
    
    /**
     * Contar reclamaciones por patrón de numeración
     */
    @Query("SELECT COUNT(r) FROM Reclamacion r WHERE r.numeroReclamacion LIKE :patron")
    long countByNumeroReclamacionLike(@Param("patron") String patron);
    
    /**
     * Verificar si existe un número específico (más eficiente que existsByNumeroReclamacion)
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reclamacion r WHERE r.numeroReclamacion = :numero")
    boolean existsReclamacionWithNumero(@Param("numero") String numero);

    // =================== MÉTODOS FALTANTES PARA REPORTES ===================
    
    // Consultas por fechas para reportes
    List<Reclamacion> findByFechaReclamacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Reclamacion> findByFechaReclamacionGreaterThanEqual(LocalDateTime fechaInicio);
    long countByFechaReclamacionGreaterThanEqual(LocalDateTime fechaInicio);
    
    // Consultas por área para reportes
    List<Reclamacion> findByAreaResponsable(String areaResponsable);
    List<Reclamacion> findByAreaResponsableAndFechaReclamacionBetween(String areaResponsable, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Consultas para dashboard
    @Query("SELECT r FROM Reclamacion r ORDER BY r.fechaReclamacion DESC")
    List<Reclamacion> findTop5ByOrderByFechaReclamacionDesc();
    
    // Métodos adicionales que se pueden necesitar
    List<Reclamacion> findByTipoMotivo(TipoMotivo tipoMotivo);
    List<Reclamacion> findByAsignadoA(String asignadoA);
    
    // Consultas específicas para estadísticas de reportes
    @Query("SELECT COUNT(r) FROM Reclamacion r WHERE r.estado = :estado AND r.fechaReclamacion BETWEEN :fechaInicio AND :fechaFin")
    long countByEstadoAndFechaReclamacionBetween(@Param("estado") EstadoReclamacion estado, 
                                                @Param("fechaInicio") LocalDateTime fechaInicio, 
                                                @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COUNT(r) FROM Reclamacion r WHERE r.areaResponsable = :area AND r.fechaReclamacion BETWEEN :fechaInicio AND :fechaFin")
    long countByAreaResponsableAndFechaReclamacionBetween(@Param("area") String areaResponsable, 
                                                         @Param("fechaInicio") LocalDateTime fechaInicio, 
                                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    // Consulta para encontrar reclamaciones vencidas
    @Query("SELECT r FROM Reclamacion r WHERE r.fechaLimiteRespuesta < :fecha AND r.estado NOT IN :estadosExcluidos")
    List<Reclamacion> findVencidas(@Param("fecha") LocalDateTime fecha, @Param("estadosExcluidos") List<EstadoReclamacion> estadosExcluidos);
    
    // Consultas para métricas de tiempo
    @Query("SELECT r FROM Reclamacion r WHERE r.estado = :estado AND r.fechaReclamacion >= :fechaInicio")
    List<Reclamacion> findByEstadoAndFechaReclamacionGreaterThanEqual(@Param("estado") EstadoReclamacion estado, 
                                                                     @Param("fechaInicio") LocalDateTime fechaInicio);
    
    // Consulta para obtener reclamaciones con prioridad específica
    List<Reclamacion> findByPrioridad(PrioridadReclamacion prioridad);
    
    // Consultas adicionales para análisis de tendencias
    @Query("SELECT r FROM Reclamacion r WHERE YEAR(r.fechaReclamacion) = :anio AND MONTH(r.fechaReclamacion) = :mes")
    List<Reclamacion> findByAnioAndMes(@Param("anio") int anio, @Param("mes") int mes);
    
    @Query("SELECT r FROM Reclamacion r WHERE DATE(r.fechaReclamacion) = DATE(:fecha)")
    List<Reclamacion> findByFechaReclamacion(@Param("fecha") LocalDateTime fecha);
    
    // Consulta para top usuarios con más reclamaciones asignadas
    @Query("SELECT r.asignadoA, COUNT(r) FROM Reclamacion r WHERE r.asignadoA IS NOT NULL GROUP BY r.asignadoA ORDER BY COUNT(r) DESC")
    List<Object[]> findTopUsuariosByReclamacionesAsignadas();
    
    // Consulta para top áreas con más reclamaciones
    @Query("SELECT r.areaResponsable, COUNT(r) FROM Reclamacion r WHERE r.areaResponsable IS NOT NULL GROUP BY r.areaResponsable ORDER BY COUNT(r) DESC")
    List<Object[]> findTopAreasByReclamaciones();
    
    // Método para consultas complejas con múltiples filtros
    @Query("SELECT r FROM Reclamacion r WHERE " +
           "(:estado IS NULL OR r.estado = :estado) AND " +
           "(:fechaInicio IS NULL OR r.fechaReclamacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR r.fechaReclamacion <= :fechaFin) AND " +
           "(:tipoMotivo IS NULL OR r.tipoMotivo = :tipoMotivo) AND " +
           "(:areaResponsable IS NULL OR r.areaResponsable = :areaResponsable)")
    List<Reclamacion> findWithFilters(@Param("estado") EstadoReclamacion estado,
                                     @Param("fechaInicio") LocalDateTime fechaInicio,
                                     @Param("fechaFin") LocalDateTime fechaFin,
                                     @Param("tipoMotivo") TipoMotivo tipoMotivo,
                                     @Param("areaResponsable") String areaResponsable);
}
