package studio.devbyjose.healthyme_reclamaciones.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import studio.devbyjose.healthyme_reclamaciones.dto.CreateRespuestaDTO;
import studio.devbyjose.healthyme_reclamaciones.dto.RespuestaReclamacionDTO;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoRespuesta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de respuestas a reclamaciones
 */
public interface RespuestaReclamacionService {

    // =================== OPERACIONES CRUD ===================
    
    /**
     * Crear una nueva respuesta
     */
    RespuestaReclamacionDTO crearRespuesta(CreateRespuestaDTO createDTO);
    
    /**
     * Obtener respuesta por ID
     */
    RespuestaReclamacionDTO obtenerPorId(Long id);
    
    /**
     * Actualizar una respuesta existente
     */
    RespuestaReclamacionDTO actualizarRespuesta(Long id, CreateRespuestaDTO updateDTO);
    
    /**
     * Eliminar una respuesta
     */
    void eliminarRespuesta(Long id);

    // =================== CONSULTAS POR RECLAMACIÓN ===================
    
    /**
     * Obtener todas las respuestas de una reclamación
     */
    List<RespuestaReclamacionDTO> obtenerRespuestasPorReclamacion(Long idReclamacion);
    
    /**
     * Obtener respuestas paginadas de una reclamación
     */
    Page<RespuestaReclamacionDTO> obtenerRespuestasPorReclamacionPaginadas(Long idReclamacion, Pageable pageable);
    
    /**
     * Obtener respuestas por tipo de una reclamación específica
     */
    List<RespuestaReclamacionDTO> obtenerRespuestasPorTipo(Long idReclamacion, TipoRespuesta tipoRespuesta);

    // =================== CONSULTAS ESPECIALIZADAS ===================
    
    /**
     * Obtener la primera respuesta de una reclamación
     */
    Optional<RespuestaReclamacionDTO> obtenerPrimeraRespuesta(Long idReclamacion);
    
    /**
     * Obtener la última respuesta de una reclamación
     */
    Optional<RespuestaReclamacionDTO> obtenerUltimaRespuesta(Long idReclamacion);
    
    /**
     * Obtener la respuesta final de una reclamación
     */
    Optional<RespuestaReclamacionDTO> obtenerRespuestaFinal(Long idReclamacion);

    // =================== OPERACIONES DE NEGOCIO ===================
    
    /**
     * Crear respuesta inicial
     */
    RespuestaReclamacionDTO crearRespuestaInicial(Long idReclamacion, String contenidoRespuesta, 
                                                 String usuarioResponsable, String areaResponsable);
    
    /**
     * Crear seguimiento
     */
    RespuestaReclamacionDTO crearSeguimiento(Long idReclamacion, String contenidoSeguimiento, 
                                            String usuarioResponsable, String areaResponsable);
    
    /**
     * Crear respuesta final
     */
    RespuestaReclamacionDTO crearRespuestaFinal(Long idReclamacion, String solucionAplicada, 
                                               String usuarioResponsable, String areaResponsable);
    
    /**
     * Marcar respuesta como confirmada
     */
    RespuestaReclamacionDTO marcarComoConfirmada(Long idRespuesta, String confirmadoPor);
    
    /**
     * Solicitar información adicional
     */
    RespuestaReclamacionDTO solicitarInformacionAdicional(Long idReclamacion, String informacionSolicitada, 
                                                         String usuarioResponsable);

    // =================== CONSULTAS AVANZADAS ===================
    
    /**
     * Buscar respuestas por contenido
     */
    Page<RespuestaReclamacionDTO> buscarRespuestasPorContenido(String textoBusqueda, Pageable pageable);
    
    /**
     * Obtener respuestas por usuario responsable
     */
    Page<RespuestaReclamacionDTO> obtenerRespuestasPorUsuario(String usuarioResponsable, Pageable pageable);
    
    /**
     * Obtener respuestas por área responsable
     */
    Page<RespuestaReclamacionDTO> obtenerRespuestasPorArea(String areaResponsable, Pageable pageable);
    
    /**
     * Obtener respuestas por rango de fechas
     */
    Page<RespuestaReclamacionDTO> obtenerRespuestasPorFecha(LocalDateTime fechaDesde, LocalDateTime fechaHasta, 
                                                           Pageable pageable);
    
    /**
     * Obtener respuestas pendientes de confirmación
     */
    List<RespuestaReclamacionDTO> obtenerRespuestasPendientesConfirmacion();

    // =================== ESTADÍSTICAS Y CONTADORES ===================
    
    /**
     * Contar respuestas por reclamación
     */
    long contarRespuestasPorReclamacion(Long idReclamacion);
    
    /**
     * Contar respuestas por tipo
     */
    long contarRespuestasPorTipo(TipoRespuesta tipoRespuesta);
    
    /**
     * Contar respuestas por usuario
     */
    long contarRespuestasPorUsuario(String usuarioResponsable);
    
    /**
     * Contar respuestas pendientes de confirmación
     */
    long contarRespuestasPendientesConfirmacion();
    
    /**
     * Verificar si tiene respuesta final
     */
    boolean tieneRespuestaFinal(Long idReclamacion);
    
    /**
     * Verificar si tiene respuestas
     */
    boolean tieneRespuestas(Long idReclamacion);

    // =================== CÁLCULOS DE TIEMPO ===================
    
    /**
     * Calcular tiempo de respuesta en horas
     */
    long calcularTiempoRespuestaEnHoras(Long idReclamacion);
    
    /**
     * Calcular tiempo de resolución en horas
     */
    long calcularTiempoResolucionEnHoras(Long idReclamacion);

    // =================== VALIDACIONES ===================
    
    /**
     * Verificar si se puede crear respuesta
     */
    boolean puedeCrearRespuesta(Long idReclamacion);
}
