package studio.devbyjose.healthyme_reclamaciones.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.entity.Reclamacion;
import studio.devbyjose.healthyme_reclamaciones.entity.RespuestaReclamacion;
import studio.devbyjose.healthyme_reclamaciones.entity.SeguimientoReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import studio.devbyjose.healthyme_reclamaciones.exception.*;
import studio.devbyjose.healthyme_reclamaciones.mapper.RespuestaReclamacionMapper;
import studio.devbyjose.healthyme_reclamaciones.repository.ReclamacionRepository;
import studio.devbyjose.healthyme_reclamaciones.repository.RespuestaReclamacionRepository;
import studio.devbyjose.healthyme_reclamaciones.repository.SeguimientoReclamacionRepository;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.RespuestaReclamacionService;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.NotificationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de respuestas a reclamaciones
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RespuestaReclamacionServiceImpl implements RespuestaReclamacionService {

    private final RespuestaReclamacionRepository respuestaRepository;
    private final ReclamacionRepository reclamacionRepository;
    private final SeguimientoReclamacionRepository seguimientoRepository;
    private final RespuestaReclamacionMapper respuestaMapper;
    private final NotificationService notificationService;

    // =================== OPERACIONES CRUD ===================

    @Override
    public RespuestaReclamacionDTO crearRespuesta(CreateRespuestaDTO createDTO) {
        log.info("Creando respuesta para reclamación ID: {}", createDTO.getReclamacionId());

        try {
            // Validar datos de entrada
            validarDatosCreacion(createDTO);

            // Obtener la reclamación
            Reclamacion reclamacion = reclamacionRepository.findById(createDTO.getReclamacionId())
                    .orElseThrow(() -> new ReclamacionNotFoundException(createDTO.getReclamacionId()));

            // Validar que se pueda responder
            validarPuedeResponder(reclamacion);

            // Mapear a entidad
            RespuestaReclamacion respuesta = respuestaMapper.toEntity(createDTO);
            respuesta.setReclamacion(reclamacion);

            // Establecer valores por defecto
            establecerValoresPorDefecto(respuesta, createDTO);

            // Actualizar estado de la reclamación según el tipo de respuesta
            actualizarEstadoReclamacion(reclamacion, respuesta);

            // Guardar respuesta
            RespuestaReclamacion respuestaGuardada = respuestaRepository.save(respuesta);

            // Crear seguimiento
            crearSeguimientoRespuesta(reclamacion, respuestaGuardada);

            // Guardar cambios en reclamación
            reclamacionRepository.save(reclamacion);

            RespuestaReclamacionDTO respuestaDTO = respuestaMapper.toDTO(respuestaGuardada);

            // Enviar notificaciones
            try {
                enviarNotificacionesRespuesta(respuestaDTO, reclamacion);
            } catch (Exception e) {
                log.warn("Error al enviar notificaciones para respuesta ID {}: {}", 
                        respuestaGuardada.getId(), e.getMessage());
            }

            log.info("Respuesta creada exitosamente: ID {}", respuestaGuardada.getId());
            return respuestaDTO;

        } catch (ReclamacionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al crear respuesta para reclamación {}: {}", 
                    createDTO.getReclamacionId(), e.getMessage(), e);
            throw new RespuestaReclamacionException("Error al crear la respuesta: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaReclamacionDTO obtenerPorId(Long id) {
        log.debug("Buscando respuesta por ID: {}", id);

        RespuestaReclamacion respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new RespuestaReclamacionException("Respuesta no encontrada con ID: " + id));

        return respuestaMapper.toDTO(respuesta);
    }

    @Override
    public RespuestaReclamacionDTO actualizarRespuesta(Long id, CreateRespuestaDTO updateDTO) {
        log.info("Actualizando respuesta ID: {}", id);

        try {
            RespuestaReclamacion respuesta = respuestaRepository.findById(id)
                    .orElseThrow(() -> new RespuestaReclamacionException("Respuesta no encontrada con ID: " + id));

            // Validar si se puede actualizar
            validarActualizacion(respuesta);

            // Actualizar campos
            actualizarCampos(respuesta, updateDTO);

            // Guardar cambios
            RespuestaReclamacion respuestaActualizada = respuestaRepository.save(respuesta);

            log.info("Respuesta actualizada exitosamente: ID {}", id);
            return respuestaMapper.toDTO(respuestaActualizada);

        } catch (RespuestaReclamacionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al actualizar respuesta ID {}: {}", id, e.getMessage(), e);
            throw new RespuestaReclamacionException("Error al actualizar la respuesta: " + e.getMessage());
        }
    }

    @Override
    public void eliminarRespuesta(Long id) {
        log.info("Eliminando respuesta ID: {}", id);

        RespuestaReclamacion respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new RespuestaReclamacionException("Respuesta no encontrada con ID: " + id));

        // Validar si se puede eliminar
        validarEliminacion(respuesta);

        respuestaRepository.delete(respuesta);

        log.info("Respuesta eliminada exitosamente: ID {}", id);
    }

    // =================== CONSULTAS POR RECLAMACIÓN ===================

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaReclamacionDTO> obtenerRespuestasPorReclamacion(Long idReclamacion) {
        log.debug("Obteniendo respuestas de la reclamación: {}", idReclamacion);

        // Verificar que la reclamación existe
        if (!reclamacionRepository.existsById(idReclamacion)) {
            throw new ReclamacionNotFoundException(idReclamacion);
        }

        List<RespuestaReclamacion> respuestas = respuestaRepository.findByReclamacionIdOrderByFechaRespuesta(idReclamacion);
        
        return respuestas.stream()
                .map(respuestaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaReclamacionDTO> obtenerRespuestasPorReclamacionPaginadas(Long idReclamacion, Pageable pageable) {
        log.debug("Obteniendo respuestas paginadas de la reclamación: {}", idReclamacion);

        // Verificar que la reclamación existe
        if (!reclamacionRepository.existsById(idReclamacion)) {
            throw new ReclamacionNotFoundException(idReclamacion);
        }

        Page<RespuestaReclamacion> respuestas = respuestaRepository.findByReclamacionIdOrderByFechaRespuesta(idReclamacion, pageable);
        
        return mapearPaginaRespuestas(respuestas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaReclamacionDTO> obtenerRespuestasPorTipo(Long idReclamacion, TipoRespuesta tipoRespuesta) {
        log.debug("Obteniendo respuestas tipo {} de la reclamación: {}", tipoRespuesta, idReclamacion);

        // Verificar que la reclamación existe
        if (!reclamacionRepository.existsById(idReclamacion)) {
            throw new ReclamacionNotFoundException(idReclamacion);
        }

        List<RespuestaReclamacion> respuestas = respuestaRepository.findByReclamacionIdAndTipoRespuestaOrderByFechaRespuesta(
                idReclamacion, tipoRespuesta);
        
        return respuestas.stream()
                .map(respuestaMapper::toDTO)
                .toList();
    }

    // =================== CONSULTAS ESPECIALIZADAS ===================

    @Override
    @Transactional(readOnly = true)
    public Optional<RespuestaReclamacionDTO> obtenerPrimeraRespuesta(Long idReclamacion) {
        log.debug("Obteniendo primera respuesta de la reclamación: {}", idReclamacion);

        Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository.findFirstByReclamacionIdOrderByFechaRespuesta(idReclamacion);
        
        return primeraRespuesta.map(respuestaMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RespuestaReclamacionDTO> obtenerUltimaRespuesta(Long idReclamacion) {
        log.debug("Obteniendo última respuesta de la reclamación: {}", idReclamacion);

        Optional<RespuestaReclamacion> ultimaRespuesta = respuestaRepository.findFirstByReclamacionIdOrderByFechaRespuestaDesc(idReclamacion);
        
        return ultimaRespuesta.map(respuestaMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RespuestaReclamacionDTO> obtenerRespuestaFinal(Long idReclamacion) {
        log.debug("Obteniendo respuesta final de la reclamación: {}", idReclamacion);

        Optional<RespuestaReclamacion> respuestaFinal = respuestaRepository.findByReclamacionIdAndTipoRespuesta(
                idReclamacion, TipoRespuesta.FINAL);
        
        return respuestaFinal.map(respuestaMapper::toDTO);
    }

    // =================== OPERACIONES DE NEGOCIO ===================

    @Override
    public RespuestaReclamacionDTO crearRespuestaInicial(Long idReclamacion, String contenidoRespuesta, 
                                                        String usuarioResponsable, String areaResponsable) {
        log.info("Creando respuesta inicial para reclamación: {}", idReclamacion);

        CreateRespuestaDTO createDTO = CreateRespuestaDTO.builder()
                .reclamacionId(idReclamacion)
                .tipoRespuesta(TipoRespuesta.INICIAL)
                .contenido(contenidoRespuesta) // Usar 'contenido' no 'contenidoRespuesta'
                .responsable(usuarioResponsable) // Usar 'responsable' no 'usuarioResponsable'
                .esRespuestaFinal(false)
                .notificarCliente(true)
                .build();

        return crearRespuesta(createDTO);
    }

    @Override
    public RespuestaReclamacionDTO crearSeguimiento(Long idReclamacion, String contenidoSeguimiento, 
                                                   String usuarioResponsable, String areaResponsable) {
        log.info("Creando seguimiento para reclamación: {}", idReclamacion);

        CreateRespuestaDTO createDTO = CreateRespuestaDTO.builder()
                .reclamacionId(idReclamacion)
                .tipoRespuesta(TipoRespuesta.SEGUIMIENTO)
                .contenido(contenidoSeguimiento) // Usar 'contenido'
                .responsable(usuarioResponsable) // Usar 'responsable'
                .esRespuestaFinal(false)
                .notificarCliente(true)
                .build();

        return crearRespuesta(createDTO);
    }

    @Override
    public RespuestaReclamacionDTO crearRespuestaFinal(Long idReclamacion, String solucionAplicada, 
                                                      String usuarioResponsable, String areaResponsable) {
        log.info("Creando respuesta final para reclamación: {}", idReclamacion);

        // Verificar que no existe ya una respuesta final
        Optional<RespuestaReclamacionDTO> respuestaExistente = obtenerRespuestaFinal(idReclamacion);
        if (respuestaExistente.isPresent()) {
            throw new RespuestaReclamacionException("Ya existe una respuesta final para esta reclamación");
        }

        CreateRespuestaDTO createDTO = CreateRespuestaDTO.builder()
                .reclamacionId(idReclamacion)
                .tipoRespuesta(TipoRespuesta.FINAL)
                .contenido(solucionAplicada) // Usar 'contenido'
                .responsable(usuarioResponsable) // Usar 'responsable'
                .esRespuestaFinal(true)
                .solucionAplicada(solucionAplicada)
                .notificarCliente(true)
                .build();

        return crearRespuesta(createDTO);
    }

    @Override
    public RespuestaReclamacionDTO marcarComoConfirmada(Long idRespuesta, String confirmadoPor) {
        log.info("Marcando respuesta como confirmada: ID {}", idRespuesta);

        RespuestaReclamacion respuesta = respuestaRepository.findById(idRespuesta)
                .orElseThrow(() -> new RespuestaReclamacionException("Respuesta no encontrada con ID: " + idRespuesta));

        // Si es respuesta final, cambiar estado de reclamación a RESUELTO
        if (respuesta.getTipoRespuesta() == TipoRespuesta.FINAL || respuesta.getEsRespuestaFinal()) {
            Reclamacion reclamacion = respuesta.getReclamacion();
            reclamacion.setEstado(EstadoReclamacion.RESUELTO);
            reclamacionRepository.save(reclamacion);

            // Crear seguimiento
            crearSeguimientoConfirmacion(reclamacion, respuesta, confirmadoPor);
        }

        RespuestaReclamacion respuestaConfirmada = respuestaRepository.save(respuesta);

        log.info("Respuesta confirmada exitosamente: ID {}", idRespuesta);
        return respuestaMapper.toDTO(respuestaConfirmada);
    }

    @Override
    public RespuestaReclamacionDTO solicitarInformacionAdicional(Long idReclamacion, String informacionSolicitada, 
                                                               String usuarioResponsable) {
        log.info("Solicitando información adicional para reclamación: {}", idReclamacion);

        // Obtener la reclamación y cambiar estado
        Reclamacion reclamacion = reclamacionRepository.findById(idReclamacion)
                .orElseThrow(() -> new ReclamacionNotFoundException(idReclamacion));

        reclamacion.setEstado(EstadoReclamacion.PENDIENTE_INFORMACION);
        reclamacionRepository.save(reclamacion);

        CreateRespuestaDTO createDTO = CreateRespuestaDTO.builder()
                .reclamacionId(idReclamacion)
                .tipoRespuesta(TipoRespuesta.SOLICITUD_INFORMACION)
                .contenido("SOLICITUD DE INFORMACIÓN ADICIONAL:\n\n" + informacionSolicitada) // Usar 'contenido'
                .responsable(usuarioResponsable) // Usar 'responsable'
                .esRespuestaFinal(false)
                .notificarCliente(true)
                .build();

        return crearRespuesta(createDTO);
    }

    // =================== CONSULTAS AVANZADAS ===================

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaReclamacionDTO> buscarRespuestasPorContenido(String textoBusqueda, Pageable pageable) {
        log.debug("Buscando respuestas por contenido: {}", textoBusqueda);

        if (!StringUtils.hasText(textoBusqueda)) {
            return Page.empty(pageable);
        }

        Page<RespuestaReclamacion> respuestas = respuestaRepository.findByContenidoContainingIgnoreCase(
                textoBusqueda, pageable);
        
        return mapearPaginaRespuestas(respuestas);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaReclamacionDTO> obtenerRespuestasPorUsuario(String usuarioResponsable, Pageable pageable) {
        log.debug("Obteniendo respuestas del usuario: {}", usuarioResponsable);

        Page<RespuestaReclamacion> respuestas = respuestaRepository.findByResponsableOrderByFechaRespuestaDesc(
                usuarioResponsable, pageable);
        
        return mapearPaginaRespuestas(respuestas);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaReclamacionDTO> obtenerRespuestasPorArea(String areaResponsable, Pageable pageable) {
        log.debug("Obteniendo respuestas del área: {}", areaResponsable);

        // Como no hay campo areaResponsable en la entidad, filtraremos por responsable
        Page<RespuestaReclamacion> respuestas = respuestaRepository.findByResponsableOrderByFechaRespuestaDesc(
                areaResponsable, pageable);
        
        return mapearPaginaRespuestas(respuestas);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaReclamacionDTO> obtenerRespuestasPorFecha(LocalDateTime fechaDesde, LocalDateTime fechaHasta, 
                                                                  Pageable pageable) {
        log.debug("Obteniendo respuestas entre {} y {}", fechaDesde, fechaHasta);

        Page<RespuestaReclamacion> respuestas = respuestaRepository.findByFechaRespuestaBetweenOrderByFechaRespuestaDesc(
                fechaDesde, fechaHasta, pageable);
        
        return mapearPaginaRespuestas(respuestas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaReclamacionDTO> obtenerRespuestasPendientesConfirmacion() {
        log.debug("Obteniendo respuestas pendientes de confirmación");

        // Como no hay campos de confirmación, obtenemos las respuestas finales no notificadas
        List<RespuestaReclamacion> respuestas = respuestaRepository.findByEsRespuestaFinalTrueAndNotificadoClienteFalse();
        
        return respuestas.stream()
                .map(respuestaMapper::toDTO)
                .toList();
    }

    // =================== ESTADÍSTICAS Y CONTADORES ===================

    @Override
    @Transactional(readOnly = true)
    public long contarRespuestasPorReclamacion(Long idReclamacion) {
        return respuestaRepository.countByReclamacionId(idReclamacion);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarRespuestasPorTipo(TipoRespuesta tipoRespuesta) {
        return respuestaRepository.countByTipoRespuesta(tipoRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarRespuestasPorUsuario(String usuarioResponsable) {
        return respuestaRepository.countByResponsable(usuarioResponsable);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarRespuestasPendientesConfirmacion() {
        return respuestaRepository.countByEsRespuestaFinalTrueAndNotificadoClienteFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneRespuestaFinal(Long idReclamacion) {
        return respuestaRepository.existsByReclamacionIdAndTipoRespuesta(idReclamacion, TipoRespuesta.FINAL) ||
               respuestaRepository.existsByReclamacionIdAndEsRespuestaFinalTrue(idReclamacion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneRespuestas(Long idReclamacion) {
        return respuestaRepository.existsByReclamacionId(idReclamacion);
    }

    // =================== CÁLCULOS DE TIEMPO ===================

    @Override
    @Transactional(readOnly = true)
    public long calcularTiempoRespuestaEnHoras(Long idReclamacion) {
        Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository.findFirstByReclamacionIdOrderByFechaRespuesta(idReclamacion);
        
        if (primeraRespuesta.isEmpty()) {
            return -1; // No hay respuestas
        }

        Reclamacion reclamacion = reclamacionRepository.findById(idReclamacion)
                .orElseThrow(() -> new ReclamacionNotFoundException(idReclamacion));

        return ChronoUnit.HOURS.between(
                reclamacion.getFechaReclamacion(), 
                primeraRespuesta.get().getFechaRespuesta()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long calcularTiempoResolucionEnHoras(Long idReclamacion) {
        Optional<RespuestaReclamacion> respuestaFinal = respuestaRepository.findByReclamacionIdAndTipoRespuesta(
                idReclamacion, TipoRespuesta.FINAL);
        
        if (respuestaFinal.isEmpty()) {
            // Buscar por campo esRespuestaFinal
            Optional<RespuestaReclamacion> respuestasFinales = respuestaRepository.findByReclamacionIdAndEsRespuestaFinalTrue(idReclamacion);
            if (respuestasFinales.isEmpty()) {
                return -1; // No está resuelta
            }
            respuestaFinal = respuestasFinales;
        }

        Reclamacion reclamacion = reclamacionRepository.findById(idReclamacion)
                .orElseThrow(() -> new ReclamacionNotFoundException(idReclamacion));

        return ChronoUnit.HOURS.between(
                reclamacion.getFechaReclamacion(), 
                respuestaFinal.get().getFechaRespuesta()
        );
    }

    // =================== VALIDACIONES ===================

    @Override
    @Transactional(readOnly = true)
    public boolean puedeCrearRespuesta(Long idReclamacion) {
        try {
            Reclamacion reclamacion = reclamacionRepository.findById(idReclamacion)
                    .orElse(null);
            
            if (reclamacion == null) {
                return false;
            }

            // No se puede responder si está cerrada o anulada
            return !Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO)
                    .contains(reclamacion.getEstado());
        } catch (Exception e) {
            log.warn("Error al validar si se puede crear respuesta para reclamación {}: {}", 
                    idReclamacion, e.getMessage());
            return false;
        }
    }

    // =================== MÉTODOS PRIVADOS DE APOYO ===================

    private void validarDatosCreacion(CreateRespuestaDTO createDTO) {
        if (!StringUtils.hasText(createDTO.getContenido())) { // Usar 'contenido'
            throw new RespuestaReclamacionException("El contenido de la respuesta es obligatorio");
        }

        if (createDTO.getTipoRespuesta() == null) {
            throw new RespuestaReclamacionException("El tipo de respuesta es obligatorio");
        }

        if (!StringUtils.hasText(createDTO.getResponsable())) { // Usar 'responsable'
            throw new RespuestaReclamacionException("El responsable es obligatorio");
        }
    }

    private void validarPuedeResponder(Reclamacion reclamacion) {
        if (Arrays.asList(EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO).contains(reclamacion.getEstado())) {
            throw new RespuestaReclamacionException(
                    "No se puede responder a una reclamación " + reclamacion.getEstado().getDescripcion().toLowerCase());
        }
    }

    private void establecerValoresPorDefecto(RespuestaReclamacion respuesta, CreateRespuestaDTO createDTO) {
        LocalDateTime ahora = LocalDateTime.now();
        
        // Solo establecer campos que existan en la entidad
        if (respuesta.getFechaRespuesta() == null) {
            respuesta.setFechaRespuesta(ahora);
        }
        
        // Valores por defecto usando campos existentes
        if (respuesta.getNotificadoCliente() == null) {
            respuesta.setNotificadoCliente(false);
        }
        
        if (respuesta.getEsRespuestaFinal() == null) {
            respuesta.setEsRespuestaFinal(createDTO.getEsRespuestaFinal() != null ? createDTO.getEsRespuestaFinal() : false);
        }
    }

    private void actualizarEstadoReclamacion(Reclamacion reclamacion, RespuestaReclamacion respuesta) {
        EstadoReclamacion estadoAnterior = reclamacion.getEstado();

        switch (respuesta.getTipoRespuesta()) {
            case INICIAL:
                if (estadoAnterior == EstadoReclamacion.RECIBIDO) {
                    reclamacion.setEstado(EstadoReclamacion.EN_PROCESO);
                }
                break;
            case FINAL:
                // La respuesta final no cambia el estado automáticamente
                break;
            case SOLICITUD_INFORMACION:
                reclamacion.setEstado(EstadoReclamacion.PENDIENTE_INFORMACION);
                break;
            default:
                // SEGUIMIENTO y otros no cambian el estado
                break;
        }
    }

    private void crearSeguimientoRespuesta(Reclamacion reclamacion, RespuestaReclamacion respuesta) {
        SeguimientoReclamacion seguimiento = new SeguimientoReclamacion();
        seguimiento.setReclamacion(reclamacion);
        seguimiento.setEstadoAnterior(null);
        seguimiento.setEstadoNuevo(reclamacion.getEstado());
        seguimiento.setComentario(generarComentarioSeguimiento(respuesta));
        seguimiento.setUsuarioResponsable(respuesta.getResponsable()); // Usar 'responsable'
        
        seguimientoRepository.save(seguimiento);
    }

    private void crearSeguimientoConfirmacion(Reclamacion reclamacion, RespuestaReclamacion respuesta, String confirmadoPor) {
        SeguimientoReclamacion seguimiento = new SeguimientoReclamacion();
        seguimiento.setReclamacion(reclamacion);
        seguimiento.setEstadoAnterior(EstadoReclamacion.EN_PROCESO);
        seguimiento.setEstadoNuevo(EstadoReclamacion.RESUELTO);
        seguimiento.setComentario("Respuesta final confirmada - Reclamación resuelta");
        seguimiento.setUsuarioResponsable(confirmadoPor);
        
        seguimientoRepository.save(seguimiento);
    }

    private String generarComentarioSeguimiento(RespuestaReclamacion respuesta) {
        return switch (respuesta.getTipoRespuesta()) {
            case INICIAL -> "Respuesta inicial proporcionada";
            case SEGUIMIENTO -> "Seguimiento agregado";
            case FINAL -> "Respuesta final proporcionada";
            case SOLICITUD_INFORMACION -> "Solicitada información adicional";
            default -> "Respuesta agregada: " + respuesta.getTipoRespuesta().getDescripcion();
        };
    }

    private void enviarNotificacionesRespuesta(RespuestaReclamacionDTO respuesta, Reclamacion reclamacion) {
        try {
            log.info("Enviando notificación para respuesta tipo: {}", respuesta.getTipoRespuesta());
            // Implementar notificaciones según métodos disponibles en NotificationService
        } catch (Exception e) {
            log.warn("Error al enviar notificación para tipo {}: {}", 
                    respuesta.getTipoRespuesta(), e.getMessage());
        }
    }

    private void validarActualizacion(RespuestaReclamacion respuesta) {
        // Validación básica de tiempo usando fechaRespuesta
        if (respuesta.getFechaRespuesta() != null) {
            long horasDesdeCreacion = ChronoUnit.HOURS.between(respuesta.getFechaRespuesta(), LocalDateTime.now());
            if (horasDesdeCreacion > 24) {
                throw new RespuestaReclamacionException("No se puede actualizar una respuesta después de 24 horas");
            }
        }
    }

    private void validarEliminacion(RespuestaReclamacion respuesta) {
        // Validación básica de tiempo
        if (respuesta.getFechaRespuesta() != null) {
            long minutosDesdeCreacion = ChronoUnit.MINUTES.between(respuesta.getFechaRespuesta(), LocalDateTime.now());
            if (minutosDesdeCreacion > 30) {
                throw new RespuestaReclamacionException("Solo se puede eliminar una respuesta dentro de 30 minutos de su creación");
            }
        }
        
        // No eliminar respuesta final
        if (respuesta.getTipoRespuesta() == TipoRespuesta.FINAL || respuesta.getEsRespuestaFinal()) {
            throw new RespuestaReclamacionException("No se puede eliminar la respuesta final");
        }
    }

    private void actualizarCampos(RespuestaReclamacion respuesta, CreateRespuestaDTO updateDTO) {
        if (StringUtils.hasText(updateDTO.getContenido())) { // Usar 'contenido'
            respuesta.setContenido(updateDTO.getContenido());
        }
        if (StringUtils.hasText(updateDTO.getSolucionAplicada())) {
            respuesta.setSolucionAplicada(updateDTO.getSolucionAplicada());
        }
        if (StringUtils.hasText(updateDTO.getObservaciones())) {
            respuesta.setObservaciones(updateDTO.getObservaciones());
        }
        if (updateDTO.getEsRespuestaFinal() != null) {
            respuesta.setEsRespuestaFinal(updateDTO.getEsRespuestaFinal());
        }
    }

    private Page<RespuestaReclamacionDTO> mapearPaginaRespuestas(Page<RespuestaReclamacion> respuestas) {
        List<RespuestaReclamacionDTO> dtos = respuestas.getContent().stream()
                .map(respuestaMapper::toDTO)
                .toList();

        return new PageImpl<>(dtos, respuestas.getPageable(), respuestas.getTotalElements());
    }
}
