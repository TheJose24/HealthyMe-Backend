package studio.devbyjose.healthyme_reclamaciones.service.interfaces;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de reclamaciones
 * Maneja el ciclo completo de vida de las reclamaciones
 */
public interface ReclamacionService {

    // =================== OPERACIONES CRUD ===================
    
    /**
     * Crear una nueva reclamación
     * @param createDTO Datos para crear la reclamación
     * @return ReclamacionDTO Reclamación creada
     */
    ReclamacionDTO crearReclamacion(CreateReclamacionDTO createDTO);

    /**
     * Obtener reclamación por ID
     * @param id ID de la reclamación
     * @return ReclamacionDTO Reclamación encontrada
     */
    ReclamacionDTO obtenerPorId(Long id);

    /**
     * Obtener reclamación por número de reclamación
     * @param numeroReclamacion Número único de la reclamación
     * @return ReclamacionDTO Reclamación encontrada
     */
    ReclamacionDTO obtenerPorNumero(String numeroReclamacion);

    /**
     * Actualizar una reclamación existente
     * @param id ID de la reclamación
     * @param updateDTO Datos de actualización
     * @return ReclamacionDTO Reclamación actualizada
     */
    ReclamacionDTO actualizarReclamacion(Long id, UpdateReclamacionDTO updateDTO);

    /**
     * Eliminar una reclamación (soft delete)
     * @param id ID de la reclamación
     */
    void eliminarReclamacion(Long id);

    // =================== CONSULTAS PAGINADAS ===================

    /**
     * Obtener todas las reclamaciones con paginación
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones
     */
    Page<ReclamacionDTO> obtenerTodas(Pageable pageable);

    /**
     * Buscar reclamaciones por criterios múltiples
     * @param estado Estado de la reclamación
     * @param prioridad Prioridad de la reclamación
     * @param tipoMotivo Tipo de motivo
     * @param fechaDesde Fecha inicio del rango
     * @param fechaHasta Fecha fin del rango
     * @param asignadoA Usuario asignado
     * @param areaResponsable Área responsable
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones filtradas
     */
    Page<ReclamacionDTO> buscarConFiltros(
            EstadoReclamacion estado,
            PrioridadReclamacion prioridad,
            TipoMotivo tipoMotivo,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            String asignadoA,
            String areaResponsable,
            Pageable pageable
    );

    /**
     * Buscar reclamaciones por texto libre
     * @param textoBusqueda Texto a buscar en descripción y detalles
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones encontradas
     */
    Page<ReclamacionDTO> buscarPorTexto(String textoBusqueda, Pageable pageable);

    // =================== CONSULTAS POR RELACIONES ===================

    /**
     * Obtener reclamaciones por paciente
     * @param idPaciente ID del paciente
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones del paciente
     */
    Page<ReclamacionDTO> obtenerPorPaciente(Long idPaciente, Pageable pageable);

    /**
     * Obtener reclamaciones por cita médica
     * @param idCita ID de la cita
     * @return List<ReclamacionDTO> Lista de reclamaciones relacionadas
     */
    List<ReclamacionDTO> obtenerPorCita(Long idCita);

    /**
     * Obtener reclamaciones por pago
     * @param idPago ID del pago
     * @return List<ReclamacionDTO> Lista de reclamaciones relacionadas
     */
    List<ReclamacionDTO> obtenerPorPago(Long idPago);

    // =================== GESTIÓN DE ESTADOS ===================

    /**
     * Cambiar estado de una reclamación
     * @param id ID de la reclamación
     * @param nuevoEstado Nuevo estado
     * @param comentario Comentario del cambio
     * @param usuarioResponsable Usuario que realiza el cambio
     * @return ReclamacionDTO Reclamación con estado actualizado
     */
    ReclamacionDTO cambiarEstado(Long id, EstadoReclamacion nuevoEstado, 
                                String comentario, String usuarioResponsable);

    /**
     * Asignar reclamación a un usuario
     * @param id ID de la reclamación
     * @param asignadoA Usuario al que se asigna
     * @param areaResponsable Área responsable
     * @return ReclamacionDTO Reclamación asignada
     */
    ReclamacionDTO asignarReclamacion(Long id, String asignadoA, String areaResponsable);

    /**
     * Cambiar prioridad de una reclamación
     * @param id ID de la reclamación
     * @param nuevaPrioridad Nueva prioridad
     * @param justificacion Justificación del cambio
     * @return ReclamacionDTO Reclamación con prioridad actualizada
     */
    ReclamacionDTO cambiarPrioridad(Long id, PrioridadReclamacion nuevaPrioridad, 
                                   String justificacion);

    // =================== CONSULTAS DE GESTIÓN ===================

    /**
     * Obtener reclamaciones vencidas
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones vencidas
     */
    Page<ReclamacionDTO> obtenerReclamacionesVencidas(Pageable pageable);

    /**
     * Obtener reclamaciones por vencer (próximas 24-48 horas)
     * @param horasAnticipacion Horas de anticipación para considerar "por vencer"
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones por vencer
     */
    Page<ReclamacionDTO> obtenerReclamacionesPorVencer(int horasAnticipacion, Pageable pageable);

    /**
     * Obtener reclamaciones asignadas a un usuario
     * @param usuarioAsignado Usuario asignado
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones asignadas
     */
    Page<ReclamacionDTO> obtenerReclamacionesAsignadas(String usuarioAsignado, Pageable pageable);

    /**
     * Obtener reclamaciones por área responsable
     * @param areaResponsable Área responsable
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionDTO> Página de reclamaciones del área
     */
    Page<ReclamacionDTO> obtenerReclamacionesPorArea(String areaResponsable, Pageable pageable);

    // =================== OPERACIONES DE NEGOCIO ===================

    /**
     * Cerrar una reclamación
     * @param id ID de la reclamación
     * @param solucionAplicada Descripción de la solución aplicada
     * @param usuarioResponsable Usuario que cierra la reclamación
     * @return ReclamacionDTO Reclamación cerrada
     */
    ReclamacionDTO cerrarReclamacion(Long id, String solucionAplicada, String usuarioResponsable);

    /**
     * Reabrir una reclamación cerrada
     * @param id ID de la reclamación
     * @param motivo Motivo de la reapertura
     * @param usuarioResponsable Usuario que reabre la reclamación
     * @return ReclamacionDTO Reclamación reabierta
     */
    ReclamacionDTO reabrirReclamacion(Long id, String motivo, String usuarioResponsable);

    /**
     * Escalar una reclamación a supervisor
     * @param id ID de la reclamación
     * @param motivoEscalamiento Motivo del escalamiento
     * @param supervisorAsignado Supervisor al que se escala
     * @return ReclamacionDTO Reclamación escalada
     */
    ReclamacionDTO escalarReclamacion(Long id, String motivoEscalamiento, String supervisorAsignado);

    /**
     * Marcar reclamación como urgente
     * @param id ID de la reclamación
     * @param justificacion Justificación de la urgencia
     * @return ReclamacionDTO Reclamación marcada como urgente
     */
    ReclamacionDTO marcarComoUrgente(Long id, String justificacion);

    // =================== VALIDACIONES Y VERIFICACIONES ===================

    /**
     * Verificar si una reclamación existe
     * @param id ID de la reclamación
     * @return boolean True si existe, False si no
     */
    boolean existeReclamacion(Long id);

    /**
     * Verificar si un número de reclamación ya existe
     * @param numeroReclamacion Número de reclamación a verificar
     * @return boolean True si existe, False si no
     */
    boolean existeNumeroReclamacion(String numeroReclamacion);

    /**
     * Validar si se puede cambiar el estado de una reclamación
     * @param estadoActual Estado actual
     * @param estadoNuevo Estado nuevo deseado
     * @return boolean True si es válido el cambio, False si no
     */
    boolean validarCambioEstado(EstadoReclamacion estadoActual, EstadoReclamacion estadoNuevo);

    /**
     * Verificar si una reclamación está vencida
     * @param id ID de la reclamación
     * @return boolean True si está vencida, False si no
     */
    boolean estaVencida(Long id);

    // =================== CONTADORES Y ESTADÍSTICAS BÁSICAS ===================

    /**
     * Contar reclamaciones por estado
     * @param estado Estado a contar
     * @return long Cantidad de reclamaciones en ese estado
     */
    long contarPorEstado(EstadoReclamacion estado);

    /**
     * Contar reclamaciones vencidas
     * @return long Cantidad de reclamaciones vencidas
     */
    long contarReclamacionesVencidas();

    /**
     * Contar reclamaciones asignadas a un usuario
     * @param usuarioAsignado Usuario asignado
     * @return long Cantidad de reclamaciones asignadas
     */
    long contarReclamacionesAsignadas(String usuarioAsignado);

    /**
     * Contar reclamaciones por área
     * @param areaResponsable Área responsable
     * @return long Cantidad de reclamaciones del área
     */
    long contarReclamacionesPorArea(String areaResponsable);
}
