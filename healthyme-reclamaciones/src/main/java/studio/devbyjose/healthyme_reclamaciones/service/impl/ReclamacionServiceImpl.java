package studio.devbyjose.healthyme_reclamaciones.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.entity.Reclamacion;
import studio.devbyjose.healthyme_reclamaciones.entity.SeguimientoReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import studio.devbyjose.healthyme_reclamaciones.exception.*;
import studio.devbyjose.healthyme_reclamaciones.mapper.ReclamacionMapper;
import studio.devbyjose.healthyme_reclamaciones.repository.ReclamacionRepository;
import studio.devbyjose.healthyme_reclamaciones.repository.SeguimientoReclamacionRepository;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.ReclamacionService;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.NumeracionService;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.NotificationService;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de reclamaciones
 * 
 * Maneja el ciclo completo de vida de las reclamaciones:
 * - Creación y validación
 * - Gestión de estados y asignaciones
 * - Notificaciones automáticas
 * - Consultas y reportes
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReclamacionServiceImpl implements ReclamacionService {

    private final ReclamacionRepository reclamacionRepository;
    private final SeguimientoReclamacionRepository seguimientoRepository;
    private final ReclamacionMapper reclamacionMapper;
    private final NumeracionService numeracionService;
    private final NotificationService notificationService;

    // =================== OPERACIONES CRUD ===================

    @Override
    public ReclamacionDTO crearReclamacion(CreateReclamacionDTO createDTO) {
        log.info("Creando nueva reclamación: {}", createDTO.getTipoMotivo());

        try {
            // Validar datos de entrada
            validarDatosCreacion(createDTO);

            // Mapear a entidad
            Reclamacion reclamacion = reclamacionMapper.toEntity(createDTO);

            // Generar número de reclamación único
            String numeroReclamacion = numeracionService.generarNumeroReclamacion();
            reclamacion.setNumeroReclamacion(numeroReclamacion);

            // Establecer valores por defecto
            establecerValoresPorDefecto(reclamacion, createDTO);

            // Calcular fecha límite de respuesta
            calcularFechaLimiteRespuesta(reclamacion);

            // Guardar reclamación
            Reclamacion reclamacionGuardada = reclamacionRepository.save(reclamacion);

            // Crear seguimiento inicial
            crearSeguimientoInicial(reclamacionGuardada);

            // Convertir a DTO
            ReclamacionDTO reclamacionDTO = reclamacionMapper.toDTO(reclamacionGuardada);

            // Enviar notificación de recepción
            try {
                notificationService.notificarRecepcionReclamacion(reclamacionDTO);
            } catch (Exception e) {
                log.warn("Error al enviar notificación de recepción para reclamación {}: {}", 
                        numeroReclamacion, e.getMessage());
            }

            log.info("Reclamación creada exitosamente: {}", numeroReclamacion);
            return reclamacionDTO;

        } catch (Exception e) {
            log.error("Error al crear reclamación: {}", e.getMessage(), e);
            throw new ReclamacionValidationException("Error al crear la reclamación: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReclamacionDTO obtenerPorId(Long id) {
        log.debug("Buscando reclamación por ID: {}", id);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        ReclamacionDTO dto = reclamacionMapper.toDTO(reclamacion);
        calcularCamposDinamicos(dto);
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReclamacionDTO obtenerPorNumero(String numeroReclamacion) {
        log.debug("Buscando reclamación por número: {}", numeroReclamacion);

        Reclamacion reclamacion = reclamacionRepository.findByNumeroReclamacion(numeroReclamacion)
                .orElseThrow(() -> new ReclamacionNotFoundException(numeroReclamacion));

        ReclamacionDTO dto = reclamacionMapper.toDTO(reclamacion);
        calcularCamposDinamicos(dto);
        
        return dto;
    }

    @Override
    public ReclamacionDTO actualizarReclamacion(Long id, UpdateReclamacionDTO updateDTO) {
        log.info("Actualizando reclamación ID: {}", id);

        try {
            Reclamacion reclamacion = reclamacionRepository.findById(id)
                    .orElseThrow(() -> new ReclamacionNotFoundException(id));

            // Validar si se puede actualizar
            validarActualizacion(reclamacion, updateDTO);

            // Actualizar campos
            actualizarCampos(reclamacion, updateDTO);

            // Recalcular fecha límite si cambió la prioridad
            if (updateDTO.getPrioridad() != null && updateDTO.getPrioridad() != reclamacion.getPrioridad()) {
                calcularFechaLimiteRespuesta(reclamacion);
            }

            // Guardar cambios
            Reclamacion reclamacionActualizada = reclamacionRepository.save(reclamacion);

            // Crear seguimiento del cambio
            if (StringUtils.hasText(updateDTO.getComentarioActualizacion())) {
                crearSeguimientoCambio(reclamacionActualizada, updateDTO.getComentarioActualizacion());
            }

            ReclamacionDTO dto = reclamacionMapper.toDTO(reclamacionActualizada);

            // Notificar cambio de estado si corresponde
            if (updateDTO.getEstado() != null && updateDTO.getEstado() != reclamacion.getEstado()) {
                try {
                    notificationService.notificarCambioEstado(dto, reclamacion.getEstado(), updateDTO.getComentarioActualizacion());
                } catch (Exception e) {
                    log.warn("Error al enviar notificación de cambio de estado: {}", e.getMessage());
                }
            }

            log.info("Reclamación actualizada exitosamente: {}", reclamacion.getNumeroReclamacion());
            return dto;

        } catch (ReclamacionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al actualizar reclamación ID {}: {}", id, e.getMessage(), e);
            throw new ReclamacionValidationException("Error al actualizar la reclamación: " + e.getMessage());
        }
    }

    @Override
    public void eliminarReclamacion(Long id) {
        log.info("Eliminando reclamación ID: {}", id);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        // Validar si se puede eliminar
        if (Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.RESUELTO).contains(reclamacion.getEstado())) {
            throw new ReclamacionValidationException("No se puede eliminar una reclamación cerrada o resuelta");
        }

        // Cambiar estado a ANULADO en lugar de eliminar físicamente
        reclamacion.setEstado(EstadoReclamacion.ANULADO);
        reclamacionRepository.save(reclamacion);

        // Crear seguimiento de anulación
        crearSeguimientoCambio(reclamacion, "Reclamación anulada");

        log.info("Reclamación anulada exitosamente: {}", reclamacion.getNumeroReclamacion());
    }

    // =================== CONSULTAS PAGINADAS ===================

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> obtenerTodas(Pageable pageable) {
        log.debug("Obteniendo todas las reclamaciones - Página: {}", pageable.getPageNumber());

        Page<Reclamacion> reclamaciones = reclamacionRepository.findByEstadoNot(EstadoReclamacion.ANULADO, pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> buscarConFiltros(EstadoReclamacion estado, PrioridadReclamacion prioridad,
                                                TipoMotivo tipoMotivo, LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                                String asignadoA, String areaResponsable, Pageable pageable) {
        log.debug("Buscando reclamaciones con filtros - Estado: {}, Prioridad: {}", estado, prioridad);

        Specification<Reclamacion> spec = crearSpecificationFiltros(estado, prioridad, tipoMotivo, 
                                                                   fechaDesde, fechaHasta, asignadoA, areaResponsable);

        Page<Reclamacion> reclamaciones = reclamacionRepository.findAll(spec, pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> buscarPorTexto(String textoBusqueda, Pageable pageable) {
        log.debug("Buscando reclamaciones por texto: {}", textoBusqueda);

        if (!StringUtils.hasText(textoBusqueda)) {
            return obtenerTodas(pageable);
        }

        Page<Reclamacion> reclamaciones = reclamacionRepository.findByDescripcionContainingIgnoreCaseOrDetalleIncidenteContainingIgnoreCaseAndEstadoNot(
                textoBusqueda, textoBusqueda, EstadoReclamacion.ANULADO, pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    // =================== CONSULTAS POR RELACIONES ===================

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> obtenerPorPaciente(Long idPaciente, Pageable pageable) {
        log.debug("Obteniendo reclamaciones del paciente: {}", idPaciente);

        Page<Reclamacion> reclamaciones = reclamacionRepository.findByIdPacienteAndEstadoNot(
                idPaciente, EstadoReclamacion.ANULADO, pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReclamacionDTO> obtenerPorCita(Long idCita) {
        log.debug("Obteniendo reclamaciones de la cita: {}", idCita);

        List<Reclamacion> reclamaciones = reclamacionRepository.findByIdCitaAndEstadoNot(
                idCita, EstadoReclamacion.ANULADO);
        
        return reclamaciones.stream()
                .map(reclamacionMapper::toDTO)
                .peek(this::calcularCamposDinamicos)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReclamacionDTO> obtenerPorPago(Long idPago) {
        log.debug("Obteniendo reclamaciones del pago: {}", idPago);

        List<Reclamacion> reclamaciones = reclamacionRepository.findByIdPagoAndEstadoNot(
                idPago, EstadoReclamacion.ANULADO);
        
        return reclamaciones.stream()
                .map(reclamacionMapper::toDTO)
                .peek(this::calcularCamposDinamicos)
                .toList();
    }

    // =================== GESTIÓN DE ESTADOS ===================

    @Override
    public ReclamacionDTO cambiarEstado(Long id, EstadoReclamacion nuevoEstado, 
                                       String comentario, String usuarioResponsable) {
        log.info("Cambiando estado de reclamación ID: {} a {}", id, nuevoEstado);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        EstadoReclamacion estadoAnterior = reclamacion.getEstado();

        // Validar cambio de estado
        if (!validarCambioEstado(estadoAnterior, nuevoEstado)) {
            throw new ReclamacionValidationException(
                    String.format("No se puede cambiar de estado %s a %s", estadoAnterior, nuevoEstado));
        }

        // Cambiar estado
        reclamacion.setEstado(nuevoEstado);
        reclamacion.setUpdatedBy(usuarioResponsable);
        reclamacion.setUpdatedAt(LocalDateTime.now());

        // Guardar cambios
        Reclamacion reclamacionActualizada = reclamacionRepository.save(reclamacion);

        // Crear seguimiento
        String comentarioSeguimiento = StringUtils.hasText(comentario) ? comentario : 
                "Cambio de estado de " + estadoAnterior.getDescripcion() + " a " + nuevoEstado.getDescripcion();
        crearSeguimientoCambio(reclamacionActualizada, comentarioSeguimiento);

        ReclamacionDTO dto = reclamacionMapper.toDTO(reclamacionActualizada);

        // Enviar notificación
        try {
            notificationService.notificarCambioEstado(dto, estadoAnterior, comentario);
        } catch (Exception e) {
            log.warn("Error al enviar notificación de cambio de estado: {}", e.getMessage());
        }

        log.info("Estado cambiado exitosamente para reclamación: {}", reclamacion.getNumeroReclamacion());
        return dto;
    }

    @Override
    public ReclamacionDTO asignarReclamacion(Long id, String asignadoA, String areaResponsable) {
        log.info("Asignando reclamación ID: {} a usuario: {}", id, asignadoA);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        // Validar que la reclamación se pueda asignar
        if (Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO).contains(reclamacion.getEstado())) {
            throw new ReclamacionValidationException("No se puede asignar una reclamación cerrada o anulada");
        }

        String asignadoAnterior = reclamacion.getAsignadoA();
        
        // Actualizar asignación
        reclamacion.setAsignadoA(asignadoA);
        reclamacion.setAreaResponsable(areaResponsable);
        reclamacion.setUpdatedAt(LocalDateTime.now());

        // Cambiar a EN_PROCESO si está en RECIBIDO
        if (reclamacion.getEstado() == EstadoReclamacion.RECIBIDO) {
            reclamacion.setEstado(EstadoReclamacion.EN_PROCESO);
        }

        Reclamacion reclamacionActualizada = reclamacionRepository.save(reclamacion);

        // Crear seguimiento
        String comentario = String.format("Reclamación asignada a %s (área: %s)", asignadoA, areaResponsable);
        if (StringUtils.hasText(asignadoAnterior)) {
            comentario += String.format(" - Anteriormente asignada a: %s", asignadoAnterior);
        }
        crearSeguimientoCambio(reclamacionActualizada, comentario);

        ReclamacionDTO dto = reclamacionMapper.toDTO(reclamacionActualizada);

        // Enviar notificación al usuario asignado
        try {
            notificationService.notificarAsignacionReclamacion(dto, asignadoA);
        } catch (Exception e) {
            log.warn("Error al enviar notificación de asignación: {}", e.getMessage());
        }

        log.info("Reclamación asignada exitosamente: {}", reclamacion.getNumeroReclamacion());
        return dto;
    }

    @Override
    public ReclamacionDTO cambiarPrioridad(Long id, PrioridadReclamacion nuevaPrioridad, String justificacion) {
        log.info("Cambiando prioridad de reclamación ID: {} a {}", id, nuevaPrioridad);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        PrioridadReclamacion prioridadAnterior = reclamacion.getPrioridad();
        
        // Cambiar prioridad
        reclamacion.setPrioridad(nuevaPrioridad);
        reclamacion.setUpdatedAt(LocalDateTime.now());

        // Recalcular fecha límite
        calcularFechaLimiteRespuesta(reclamacion);

        Reclamacion reclamacionActualizada = reclamacionRepository.save(reclamacion);

        // Crear seguimiento
        String comentario = String.format("Prioridad cambiada de %s a %s", 
                prioridadAnterior.getDescripcion(), nuevaPrioridad.getDescripcion());
        if (StringUtils.hasText(justificacion)) {
            comentario += " - Justificación: " + justificacion;
        }
        crearSeguimientoCambio(reclamacionActualizada, comentario);

        log.info("Prioridad cambiada exitosamente para reclamación: {}", reclamacion.getNumeroReclamacion());
        return reclamacionMapper.toDTO(reclamacionActualizada);
    }

    // =================== CONSULTAS DE GESTIÓN ===================

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> obtenerReclamacionesVencidas(Pageable pageable) {
        log.debug("Obteniendo reclamaciones vencidas");

        LocalDateTime ahora = LocalDateTime.now();
        Page<Reclamacion> reclamaciones = reclamacionRepository.findByEstadoNotInAndFechaLimiteRespuestaBefore(
                Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO), ahora, pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> obtenerReclamacionesPorVencer(int horasAnticipacion, Pageable pageable) {
        log.debug("Obteniendo reclamaciones por vencer en {} horas", horasAnticipacion);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(horasAnticipacion);
        
        Page<Reclamacion> reclamaciones = reclamacionRepository.findByEstadoNotInAndFechaLimiteRespuestaBetween(
                Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO), ahora, limite, pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> obtenerReclamacionesAsignadas(String usuarioAsignado, Pageable pageable) {
        log.debug("Obteniendo reclamaciones asignadas a: {}", usuarioAsignado);

        Page<Reclamacion> reclamaciones = reclamacionRepository.findByAsignadoAAndEstadoNotIn(
                usuarioAsignado, Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO), pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamacionDTO> obtenerReclamacionesPorArea(String areaResponsable, Pageable pageable) {
        log.debug("Obteniendo reclamaciones del área: {}", areaResponsable);

        Page<Reclamacion> reclamaciones = reclamacionRepository.findByAreaResponsableAndEstadoNotIn(
                areaResponsable, Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO), pageable);
        
        return mapearPaginaReclamaciones(reclamaciones);
    }

    // =================== OPERACIONES DE NEGOCIO ===================

    @Override
    public ReclamacionDTO cerrarReclamacion(Long id, String solucionAplicada, String usuarioResponsable) {
        log.info("Cerrando reclamación ID: {}", id);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        // Validar que se pueda cerrar
        if (!Arrays.asList(EstadoReclamacion.RESUELTO, EstadoReclamacion.EN_PROCESO).contains(reclamacion.getEstado())) {
            throw new ReclamacionValidationException("Solo se pueden cerrar reclamaciones resueltas o en proceso");
        }

        EstadoReclamacion estadoAnterior = reclamacion.getEstado();

        // Cerrar reclamación
        reclamacion.setEstado(EstadoReclamacion.CERRADO);
        reclamacion.setUpdatedBy(usuarioResponsable);
        reclamacion.setUpdatedAt(LocalDateTime.now());

        Reclamacion reclamacionCerrada = reclamacionRepository.save(reclamacion);

        // Crear seguimiento de cierre
        String comentario = "Reclamación cerrada";
        if (StringUtils.hasText(solucionAplicada)) {
            comentario += " - Solución aplicada: " + solucionAplicada;
        }
        crearSeguimientoCambio(reclamacionCerrada, comentario);

        ReclamacionDTO dto = reclamacionMapper.toDTO(reclamacionCerrada);

        // Enviar notificación de cierre
        try {
            // Aquí necesitarías obtener la respuesta final si existe
            notificationService.notificarCierreReclamacion(dto, null);
        } catch (Exception e) {
            log.warn("Error al enviar notificación de cierre: {}", e.getMessage());
        }

        log.info("Reclamación cerrada exitosamente: {}", reclamacion.getNumeroReclamacion());
        return dto;
    }

    @Override
    public ReclamacionDTO reabrirReclamacion(Long id, String motivo, String usuarioResponsable) {
        log.info("Reabriendo reclamación ID: {}", id);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        // Validar que se pueda reabrir
        if (reclamacion.getEstado() != EstadoReclamacion.CERRADO) {
            throw new ReclamacionValidationException("Solo se pueden reabrir reclamaciones cerradas");
        }

        // Reabrir reclamación
        reclamacion.setEstado(EstadoReclamacion.EN_PROCESO);
        reclamacion.setUpdatedBy(usuarioResponsable);
        reclamacion.setUpdatedAt(LocalDateTime.now());

        // Recalcular fecha límite
        calcularFechaLimiteRespuesta(reclamacion);

        Reclamacion reclamacionReabierta = reclamacionRepository.save(reclamacion);

        // Crear seguimiento
        String comentario = "Reclamación reabierta";
        if (StringUtils.hasText(motivo)) {
            comentario += " - Motivo: " + motivo;
        }
        crearSeguimientoCambio(reclamacionReabierta, comentario);

        log.info("Reclamación reabierta exitosamente: {}", reclamacion.getNumeroReclamacion());
        return reclamacionMapper.toDTO(reclamacionReabierta);
    }

    @Override
    public ReclamacionDTO escalarReclamacion(Long id, String motivoEscalamiento, String supervisorAsignado) {
        log.info("Escalando reclamación ID: {} a supervisor: {}", id, supervisorAsignado);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        String asignadoAnterior = reclamacion.getAsignadoA();

        // Escalar reclamación
        reclamacion.setAsignadoA(supervisorAsignado);
        reclamacion.setPrioridad(PrioridadReclamacion.ALTA); // Escalar prioridad
        reclamacion.setUpdatedAt(LocalDateTime.now());

        // Recalcular fecha límite por cambio de prioridad
        calcularFechaLimiteRespuesta(reclamacion);

        Reclamacion reclamacionEscalada = reclamacionRepository.save(reclamacion);

        // Crear seguimiento
        String comentario = String.format("Reclamación escalada a %s", supervisorAsignado);
        if (StringUtils.hasText(asignadoAnterior)) {
            comentario += String.format(" (anteriormente asignada a %s)", asignadoAnterior);
        }
        if (StringUtils.hasText(motivoEscalamiento)) {
            comentario += " - Motivo: " + motivoEscalamiento;
        }
        crearSeguimientoCambio(reclamacionEscalada, comentario);

        ReclamacionDTO dto = reclamacionMapper.toDTO(reclamacionEscalada);

        // Enviar notificación de escalamiento
        try {
            notificationService.notificarEscalamiento(dto, supervisorAsignado, motivoEscalamiento);
        } catch (Exception e) {
            log.warn("Error al enviar notificación de escalamiento: {}", e.getMessage());
        }

        log.info("Reclamación escalada exitosamente: {}", reclamacion.getNumeroReclamacion());
        return dto;
    }

    @Override
    public ReclamacionDTO marcarComoUrgente(Long id, String justificacion) {
        log.info("Marcando como urgente reclamación ID: {}", id);

        Reclamacion reclamacion = reclamacionRepository.findById(id)
                .orElseThrow(() -> new ReclamacionNotFoundException(id));

        PrioridadReclamacion prioridadAnterior = reclamacion.getPrioridad();

        // Marcar como urgente
        reclamacion.setPrioridad(PrioridadReclamacion.CRITICA);
        reclamacion.setUpdatedAt(LocalDateTime.now());

        // Recalcular fecha límite
        calcularFechaLimiteRespuesta(reclamacion);

        Reclamacion reclamacionUrgente = reclamacionRepository.save(reclamacion);

        // Crear seguimiento
        String comentario = String.format("Marcada como URGENTE (era %s)", prioridadAnterior.getDescripcion());
        if (StringUtils.hasText(justificacion)) {
            comentario += " - Justificación: " + justificacion;
        }
        crearSeguimientoCambio(reclamacionUrgente, comentario);

        log.info("Reclamación marcada como urgente: {}", reclamacion.getNumeroReclamacion());
        return reclamacionMapper.toDTO(reclamacionUrgente);
    }

    // =================== VALIDACIONES Y VERIFICACIONES ===================

    @Override
    @Transactional(readOnly = true)
    public boolean existeReclamacion(Long id) {
        return reclamacionRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNumeroReclamacion(String numeroReclamacion) {
        return reclamacionRepository.existsByNumeroReclamacion(numeroReclamacion);
    }

    @Override
    public boolean validarCambioEstado(EstadoReclamacion estadoActual, EstadoReclamacion estadoNuevo) {
        if (estadoActual == estadoNuevo) {
            return false; // No cambio
        }

        // Reglas de transición de estados
        return switch (estadoActual) {
            case RECIBIDO -> Arrays.asList(EstadoReclamacion.EN_PROCESO, EstadoReclamacion.ANULADO).contains(estadoNuevo);
            case EN_PROCESO -> Arrays.asList(EstadoReclamacion.PENDIENTE_INFORMACION, EstadoReclamacion.RESUELTO, 
                                            EstadoReclamacion.ANULADO).contains(estadoNuevo);
            case PENDIENTE_INFORMACION -> Arrays.asList(EstadoReclamacion.EN_PROCESO, EstadoReclamacion.ANULADO).contains(estadoNuevo);
            case RESUELTO -> Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.EN_PROCESO).contains(estadoNuevo);
            case CERRADO -> estadoNuevo == EstadoReclamacion.EN_PROCESO; // Solo reabrir
            case ANULADO -> false; // No se puede cambiar desde anulado
        };
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaVencida(Long id) {
        Optional<Reclamacion> reclamacion = reclamacionRepository.findById(id);
        if (reclamacion.isEmpty()) {
            return false;
        }

        return reclamacion.get().getFechaLimiteRespuesta() != null &&
               reclamacion.get().getFechaLimiteRespuesta().isBefore(LocalDateTime.now()) &&
               !Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO).contains(reclamacion.get().getEstado());
    }

    // =================== CONTADORES Y ESTADÍSTICAS BÁSICAS ===================

    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoReclamacion estado) {
        return reclamacionRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarReclamacionesVencidas() {
        return reclamacionRepository.countByEstadoNotInAndFechaLimiteRespuestaBefore(
                Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO), LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public long contarReclamacionesAsignadas(String usuarioAsignado) {
        return reclamacionRepository.countByAsignadoAAndEstadoNotIn(
                usuarioAsignado, Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO));
    }

    @Override
    @Transactional(readOnly = true)
    public long contarReclamacionesPorArea(String areaResponsable) {
        return reclamacionRepository.countByAreaResponsableAndEstadoNotIn(
                areaResponsable, Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO));
    }

    // =================== MÉTODOS PRIVADOS DE APOYO ===================

    private void validarDatosCreacion(CreateReclamacionDTO createDTO) {
        // Validaciones adicionales de negocio
        if (createDTO.getFechaIncidente() != null && createDTO.getFechaIncidente().isAfter(LocalDateTime.now())) {
            throw new ReclamacionValidationException("La fecha del incidente no puede ser futura");
        }
    }

    private void establecerValoresPorDefecto(Reclamacion reclamacion, CreateReclamacionDTO createDTO) {
        // Estado inicial
        reclamacion.setEstado(EstadoReclamacion.RECIBIDO);
        
        // Prioridad por defecto si no se especifica
        if (reclamacion.getPrioridad() == null) {
            reclamacion.setPrioridad(PrioridadReclamacion.MEDIA);
        }

        // Requiere respuesta por defecto
        if (reclamacion.getRequiereRespuesta() == null) {
            reclamacion.setRequiereRespuesta(true);
        }

        // Generar número de hoja si no existe
        if (!StringUtils.hasText(reclamacion.getNumeroHoja())) {
            reclamacion.setNumeroHoja(numeracionService.generarNumeroHoja());
        }

        // Timestamps de auditoría
        LocalDateTime ahora = LocalDateTime.now();
        reclamacion.setCreatedAt(ahora);
        reclamacion.setUpdatedAt(ahora);
    }

    private void calcularFechaLimiteRespuesta(Reclamacion reclamacion) {
        if (reclamacion.getRequiereRespuesta() && reclamacion.getPrioridad() != null) {
            LocalDateTime fechaBase = reclamacion.getFechaReclamacion() != null ? 
                    reclamacion.getFechaReclamacion() : LocalDateTime.now();
            
            int diasRespuesta = reclamacion.getPrioridad().getDiasRespuesta();
            reclamacion.setFechaLimiteRespuesta(fechaBase.plusDays(diasRespuesta));
        }
    }

    private void crearSeguimientoInicial(Reclamacion reclamacion) {
        SeguimientoReclamacion seguimiento = new SeguimientoReclamacion();
        seguimiento.setReclamacion(reclamacion);
        seguimiento.setFechaCambio(LocalDateTime.now());
        seguimiento.setEstadoAnterior(null);
        seguimiento.setEstadoNuevo(EstadoReclamacion.RECIBIDO);
        seguimiento.setComentario("Reclamación recibida y registrada en el sistema");
        seguimiento.setUsuarioResponsable(reclamacion.getCreatedBy());
        
        seguimientoRepository.save(seguimiento);
    }

    private void crearSeguimientoCambio(Reclamacion reclamacion, String comentario) {
        SeguimientoReclamacion seguimiento = new SeguimientoReclamacion();
        seguimiento.setReclamacion(reclamacion);
        seguimiento.setFechaCambio(LocalDateTime.now());
        seguimiento.setEstadoAnterior(null); // Se podría mejorar guardando el estado anterior
        seguimiento.setEstadoNuevo(reclamacion.getEstado());
        seguimiento.setComentario(comentario);
        seguimiento.setUsuarioResponsable(reclamacion.getUpdatedBy());
        
        seguimientoRepository.save(seguimiento);
    }

    private void validarActualizacion(Reclamacion reclamacion, UpdateReclamacionDTO updateDTO) {
        // No se puede actualizar una reclamación anulada
        if (reclamacion.getEstado() == EstadoReclamacion.ANULADO) {
            throw new ReclamacionValidationException("No se puede actualizar una reclamación anulada");
        }

        // Validar cambio de estado si se especifica
        if (updateDTO.getEstado() != null && !validarCambioEstado(reclamacion.getEstado(), updateDTO.getEstado())) {
            throw new ReclamacionValidationException(
                    String.format("No se puede cambiar de estado %s a %s", 
                            reclamacion.getEstado(), updateDTO.getEstado()));
        }
    }

    private void actualizarCampos(Reclamacion reclamacion, UpdateReclamacionDTO updateDTO) {
        // Actualizar solo campos no nulos
        if (updateDTO.getTipoMotivo() != null) {
            reclamacion.setTipoMotivo(updateDTO.getTipoMotivo());
        }
        if (updateDTO.getDescripcion() != null) {
            reclamacion.setDescripcion(updateDTO.getDescripcion());
        }
        if (updateDTO.getNombreReclamante() != null) {
            reclamacion.setNombreReclamante(updateDTO.getNombreReclamante());
        }
        if (updateDTO.getEmailReclamante() != null) {
            reclamacion.setEmailReclamante(updateDTO.getEmailReclamante());
        }
        if (updateDTO.getTelefonoReclamante() != null) {
            reclamacion.setTelefonoReclamante(updateDTO.getTelefonoReclamante());
        }
        if (updateDTO.getEstado() != null) {
            reclamacion.setEstado(updateDTO.getEstado());
        }
        if (updateDTO.getPrioridad() != null) {
            reclamacion.setPrioridad(updateDTO.getPrioridad());
        }
        if (updateDTO.getAsignadoA() != null) {
            reclamacion.setAsignadoA(updateDTO.getAsignadoA());
        }
        if (updateDTO.getAreaResponsable() != null) {
            reclamacion.setAreaResponsable(updateDTO.getAreaResponsable());
        }

        // Timestamp de actualización
        reclamacion.setUpdatedAt(LocalDateTime.now());
    }

    private Specification<Reclamacion> crearSpecificationFiltros(EstadoReclamacion estado, PrioridadReclamacion prioridad,
                                                               TipoMotivo tipoMotivo, LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                                               String asignadoA, String areaResponsable) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Excluir anuladas por defecto
            predicates.add(criteriaBuilder.notEqual(root.get("estado"), EstadoReclamacion.ANULADO));

            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (prioridad != null) {
                predicates.add(criteriaBuilder.equal(root.get("prioridad"), prioridad));
            }
            if (tipoMotivo != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoMotivo"), tipoMotivo));
            }
            if (fechaDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaReclamacion"), fechaDesde));
            }
            if (fechaHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaReclamacion"), fechaHasta));
            }
            if (StringUtils.hasText(asignadoA)) {
                predicates.add(criteriaBuilder.equal(root.get("asignadoA"), asignadoA));
            }
            if (StringUtils.hasText(areaResponsable)) {
                predicates.add(criteriaBuilder.equal(root.get("areaResponsable"), areaResponsable));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Page<ReclamacionDTO> mapearPaginaReclamaciones(Page<Reclamacion> reclamaciones) {
        List<ReclamacionDTO> dtos = reclamaciones.getContent().stream()
                .map(reclamacionMapper::toDTO)
                .peek(this::calcularCamposDinamicos)
                .toList();

        return new PageImpl<>(dtos, reclamaciones.getPageable(), reclamaciones.getTotalElements());
    }

    private void calcularCamposDinamicos(ReclamacionDTO dto) {
        // Calcular si está vencida
        if (dto.getFechaLimiteRespuesta() != null) {
            dto.setEsVencida(dto.getFechaLimiteRespuesta().isBefore(LocalDateTime.now()) &&
                    !Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO).contains(dto.getEstado()));

            // Calcular días para vencer
            if (!dto.getEsVencida()) {
                dto.setDiasParaVencer(ChronoUnit.DAYS.between(LocalDateTime.now(), dto.getFechaLimiteRespuesta()));
            }
        }

        // Calcular contadores de relaciones
        if (dto.getRespuestas() != null) {
            dto.setTotalRespuestas(dto.getRespuestas().size());
        }
        if (dto.getAdjuntos() != null) {
            dto.setTotalAdjuntos(dto.getAdjuntos().size());
        }
    }
}
