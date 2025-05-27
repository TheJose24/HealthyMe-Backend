package studio.devbyjose.healthyme_reclamaciones.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoRespuesta;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.RespuestaReclamacionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para la gestión de respuestas a reclamaciones
 * Proporciona endpoints para crear, consultar y gestionar respuestas de reclamaciones
 */
@Tag(
    name = "Respuestas de Reclamaciones", 
    description = "API para la gestión de respuestas y seguimiento de reclamaciones"
)
@Slf4j
@RestController
@RequestMapping("/api/v1/respuestas")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class RespuestaReclamacionController {

    private final RespuestaReclamacionService respuestaService;

    // =================== OPERACIONES CRUD ===================

    @Operation(
        summary = "Crear nueva respuesta",
        description = "Crea una nueva respuesta para una reclamación específica con notificación automática al cliente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Respuesta creada exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto - Estado de reclamación no permite respuesta",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<RespuestaReclamacionDTO> crearRespuesta(
            @Parameter(description = "Datos para crear la respuesta", required = true)
            @Valid @RequestBody CreateRespuestaDTO createDTO) {
        
        log.info("Creando nueva respuesta para reclamación ID: {}", createDTO.getReclamacionId());
        
        RespuestaReclamacionDTO respuestaCreada = respuestaService.crearRespuesta(createDTO);
        
        log.info("Respuesta creada exitosamente con ID: {}", respuestaCreada.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaCreada);
    }

    @Operation(
        summary = "Obtener respuesta por ID",
        description = "Recupera una respuesta específica utilizando su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuesta encontrada",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Respuesta no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<RespuestaReclamacionDTO> obtenerPorId(
            @Parameter(description = "ID único de la respuesta", required = true, example = "1")
            @PathVariable @Positive Long id) {
        
        log.debug("Obteniendo respuesta por ID: {}", id);
        
        RespuestaReclamacionDTO respuesta = respuestaService.obtenerPorId(id);
        
        return ResponseEntity.ok(respuesta);
    }

    @Operation(
        summary = "Actualizar respuesta",
        description = "Actualiza el contenido de una respuesta existente (solo si no ha sido enviada al cliente)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuesta actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos o respuesta ya enviada",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Respuesta no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<RespuestaReclamacionDTO> actualizarRespuesta(
            @Parameter(description = "ID único de la respuesta", required = true, example = "1")
            @PathVariable @Positive Long id,
            @Parameter(description = "Datos de actualización", required = true)
            @Valid @RequestBody CreateRespuestaDTO updateDTO) {
        
        log.info("Actualizando respuesta ID: {}", id);
        
        RespuestaReclamacionDTO respuestaActualizada = respuestaService.actualizarRespuesta(id, updateDTO);
        
        log.info("Respuesta actualizada exitosamente: {}", id);
        
        return ResponseEntity.ok(respuestaActualizada);
    }

    @Operation(
        summary = "Eliminar respuesta",
        description = "Elimina una respuesta de forma lógica (solo si no ha sido enviada al cliente)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Respuesta eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Respuesta no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto - No se puede eliminar respuesta enviada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> eliminarRespuesta(
            @Parameter(description = "ID único de la respuesta", required = true, example = "1")
            @PathVariable @Positive Long id) {
        
        log.info("Eliminando respuesta ID: {}", id);
        
        respuestaService.eliminarRespuesta(id);
        
        log.info("Respuesta eliminada exitosamente: {}", id);
        
        return ResponseEntity.noContent().build();
    }

    // =================== CONSULTAS POR RECLAMACIÓN ===================

    @Operation(
        summary = "Obtener respuestas de una reclamación",
        description = "Recupera todas las respuestas asociadas a una reclamación específica ordenadas cronológicamente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuestas obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<List<RespuestaReclamacionDTO>> obtenerRespuestasPorReclamacion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        log.debug("Obteniendo respuestas de la reclamación: {}", reclamacionId);
        
        List<RespuestaReclamacionDTO> respuestas = respuestaService.obtenerRespuestasPorReclamacion(reclamacionId);
        
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener historial paginado de respuestas",
        description = "Recupera el historial completo de respuestas de una reclamación con paginación"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Historial obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/historial")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<RespuestaReclamacionDTO>> obtenerHistorialRespuestas(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 10, sort = "fechaRespuesta") Pageable pageable) {
        
        log.debug("Obteniendo historial de respuestas de la reclamación: {}", reclamacionId);
        
        Page<RespuestaReclamacionDTO> historial = respuestaService.obtenerRespuestasPorReclamacionPaginadas(
                reclamacionId, pageable);
        
        return ResponseEntity.ok(historial);
    }

    @Operation(
        summary = "Obtener respuestas por tipo",
        description = "Recupera las respuestas de una reclamación filtradas por tipo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuestas por tipo obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/tipo/{tipoRespuesta}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<List<RespuestaReclamacionDTO>> obtenerRespuestasPorTipo(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId,
            
            @Parameter(description = "Tipo de respuesta", required = true)
            @PathVariable TipoRespuesta tipoRespuesta) {
        
        log.debug("Obteniendo respuestas tipo {} de la reclamación: {}", tipoRespuesta, reclamacionId);
        
        List<RespuestaReclamacionDTO> respuestas = respuestaService.obtenerRespuestasPorTipo(
                reclamacionId, tipoRespuesta);
        
        return ResponseEntity.ok(respuestas);
    }

    // =================== CONSULTAS ESPECIALIZADAS ===================

    @Operation(
        summary = "Obtener primera respuesta",
        description = "Recupera la primera respuesta dada a una reclamación específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Primera respuesta encontrada",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró respuesta inicial",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/primera")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<RespuestaReclamacionDTO> obtenerPrimeraRespuesta(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        log.debug("Obteniendo primera respuesta de la reclamación: {}", reclamacionId);
        
        Optional<RespuestaReclamacionDTO> primeraRespuesta = respuestaService.obtenerPrimeraRespuesta(reclamacionId);
        
        return primeraRespuesta
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Obtener última respuesta",
        description = "Recupera la respuesta más reciente de una reclamación específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Última respuesta encontrada",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró respuesta",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/ultima")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<RespuestaReclamacionDTO> obtenerUltimaRespuesta(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        log.debug("Obteniendo última respuesta de la reclamación: {}", reclamacionId);
        
        Optional<RespuestaReclamacionDTO> ultimaRespuesta = respuestaService.obtenerUltimaRespuesta(reclamacionId);
        
        return ultimaRespuesta
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Obtener respuesta final",
        description = "Recupera la respuesta marcada como final de una reclamación (que cierra el caso)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuesta final encontrada",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró respuesta final",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/final")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<RespuestaReclamacionDTO> obtenerRespuestaFinal(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        log.debug("Obteniendo respuesta final de la reclamación: {}", reclamacionId);
        
        Optional<RespuestaReclamacionDTO> respuestaFinal = respuestaService.obtenerRespuestaFinal(reclamacionId);
        
        return respuestaFinal
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =================== OPERACIONES DE NEGOCIO ===================

    @Operation(
        summary = "Crear respuesta inicial",
        description = "Crea la primera respuesta para una reclamación con contenido y asignación específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Respuesta inicial creada exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Ya existe respuesta inicial",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/reclamacion/{reclamacion_id}/inicial")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<RespuestaReclamacionDTO> crearRespuestaInicial(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacion_id,
            
            @Parameter(description = "Contenido de la respuesta inicial", required = true)
            @RequestParam @NotBlank String contenido_respuesta,
            
            @Parameter(description = "Usuario responsable", required = true)
            @RequestParam @NotBlank String usuario_responsable,
            
            @Parameter(description = "Área responsable", required = true)
            @RequestParam @NotBlank String area_responsable) {
        
        log.info("Creando respuesta inicial para reclamación: {}", reclamacion_id);
        
        RespuestaReclamacionDTO respuestaInicial = respuestaService.crearRespuestaInicial(
                reclamacion_id, contenido_respuesta, usuario_responsable, area_responsable);
        
        log.info("Respuesta inicial creada exitosamente para reclamación: {}", reclamacion_id);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaInicial);
    }

    @Operation(
        summary = "Crear seguimiento",
        description = "Crea una respuesta de seguimiento para mantener informado al cliente del progreso"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Seguimiento creado exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/reclamacion/{reclamacion_id}/seguimiento")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<RespuestaReclamacionDTO> crearSeguimiento(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacion_id,
            
            @Parameter(description = "Contenido del seguimiento", required = true)
            @RequestParam @NotBlank String contenido_seguimiento,
            
            @Parameter(description = "Usuario responsable", required = true)
            @RequestParam @NotBlank String usuario_responsable,
            
            @Parameter(description = "Área responsable", required = true)
            @RequestParam @NotBlank String area_responsable) {
        
        log.info("Creando seguimiento para reclamación: {}", reclamacion_id);
        
        RespuestaReclamacionDTO seguimiento = respuestaService.crearSeguimiento(
                reclamacion_id, contenido_seguimiento, usuario_responsable, area_responsable);
        
        log.info("Seguimiento creado exitosamente para reclamación: {}", reclamacion_id);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(seguimiento);
    }

    @Operation(
        summary = "Crear respuesta final",
        description = "Crea la respuesta final con la solución aplicada, cerrando efectivamente la reclamación"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Respuesta final creada exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Ya existe respuesta final",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/reclamacion/{reclamacion_id}/final")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<RespuestaReclamacionDTO> crearRespuestaFinal(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacion_id,
            
            @Parameter(description = "Solución aplicada", required = true)
            @RequestParam @NotBlank String solucion_aplicada,
            
            @Parameter(description = "Usuario responsable", required = true)
            @RequestParam @NotBlank String usuario_responsable,
            
            @Parameter(description = "Área responsable", required = true)
            @RequestParam @NotBlank String area_responsable) {
        
        log.info("Creando respuesta final para reclamación: {}", reclamacion_id);
        
        RespuestaReclamacionDTO respuestaFinal = respuestaService.crearRespuestaFinal(
                reclamacion_id, solucion_aplicada, usuario_responsable, area_responsable);
        
        log.info("Respuesta final creada exitosamente para reclamación: {}", reclamacion_id);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaFinal);
    }

    @Operation(
        summary = "Marcar respuesta como confirmada",
        description = "Marca una respuesta como confirmada por el responsable o supervisor"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuesta confirmada exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Respuesta no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PatchMapping("/{id}/confirmar")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<RespuestaReclamacionDTO> marcarComoConfirmada(
            @Parameter(description = "ID de la respuesta", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Usuario que confirma", required = true)
            @RequestParam @NotBlank String confirmadoPor) {
        
        log.info("Marcando respuesta {} como confirmada por {}", id, confirmadoPor);
        
        RespuestaReclamacionDTO respuestaConfirmada = respuestaService.marcarComoConfirmada(id, confirmadoPor);
        
        log.info("Respuesta confirmada exitosamente: {}", id);
        
        return ResponseEntity.ok(respuestaConfirmada);
    }

    @Operation(
        summary = "Solicitar información adicional",
        description = "Crea una respuesta solicitando información adicional al cliente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Solicitud de información creada exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/reclamacion/{reclamacion_id}/solicitar-info")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<RespuestaReclamacionDTO> solicitarInformacionAdicional(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacion_id,
            
            @Parameter(description = "Información solicitada", required = true)
            @RequestParam @NotBlank String informacion_solicitada,
            
            @Parameter(description = "Usuario responsable", required = true)
            @RequestParam @NotBlank String usuario_responsable) {
        
        log.info("Solicitando información adicional para reclamación: {}", reclamacion_id);
        
        RespuestaReclamacionDTO solicitudInfo = respuestaService.solicitarInformacionAdicional(
                reclamacion_id, informacion_solicitada, usuario_responsable);
        
        log.info("Solicitud de información creada exitosamente para reclamación: {}", reclamacion_id);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudInfo);
    }

    // =================== CONSULTAS AVANZADAS ===================

    @Operation(
        summary = "Buscar respuestas por contenido",
        description = "Busca respuestas por texto en el contenido de la respuesta"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Búsqueda completada exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Texto de búsqueda inválido",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/buscar/contenido")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<RespuestaReclamacionDTO>> buscarPorContenido(
            @Parameter(description = "Texto a buscar en el contenido", required = true, example = "solución")
            @RequestParam @NotBlank String texto_busqueda,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaRespuesta") Pageable pageable) {
        
        log.debug("Buscando respuestas por contenido: {}", texto_busqueda);
        
        Page<RespuestaReclamacionDTO> respuestas = respuestaService.buscarRespuestasPorContenido(texto_busqueda, pageable);
        
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener respuestas por usuario",
        description = "Lista todas las respuestas creadas por un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuestas del usuario obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/usuario/{usuarioResponsable}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<RespuestaReclamacionDTO>> obtenerRespuestasPorUsuario(
            @Parameter(description = "Usuario responsable", required = true, example = "juan.perez")
            @PathVariable @NotBlank String usuarioResponsable,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaRespuesta") Pageable pageable) {
        
        log.debug("Obteniendo respuestas del usuario: {}", usuarioResponsable);
        
        Page<RespuestaReclamacionDTO> respuestas = respuestaService.obtenerRespuestasPorUsuario(
                usuarioResponsable, pageable);
        
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener respuestas por área",
        description = "Lista todas las respuestas de un área responsable específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuestas del área obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/area/{areaResponsable}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<RespuestaReclamacionDTO>> obtenerRespuestasPorArea(
            @Parameter(description = "Área responsable", required = true, example = "Atención al Cliente")
            @PathVariable @NotBlank String areaResponsable,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaRespuesta") Pageable pageable) {
        
        log.debug("Obteniendo respuestas del área: {}", areaResponsable);
        
        Page<RespuestaReclamacionDTO> respuestas = respuestaService.obtenerRespuestasPorArea(
                areaResponsable, pageable);
        
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener respuestas por fecha",
        description = "Lista las respuestas creadas en un rango de fechas específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuestas por fecha obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/fecha")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<RespuestaReclamacionDTO>> obtenerRespuestasPorFecha(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_desde,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_hasta,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaRespuesta") Pageable pageable) {
        
        log.debug("Obteniendo respuestas entre {} y {}", fecha_desde, fecha_hasta);
        
        Page<RespuestaReclamacionDTO> respuestas = respuestaService.obtenerRespuestasPorFecha(
                fecha_desde, fecha_hasta, pageable);
        
        return ResponseEntity.ok(respuestas);
    }

    @Operation(
        summary = "Obtener respuestas pendientes de confirmación",
        description = "Lista las respuestas que están pendientes de confirmación por supervisor"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Respuestas pendientes obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/pendientes-confirmacion")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<RespuestaReclamacionDTO>> obtenerRespuestasPendientesConfirmacion() {
        
        log.debug("Obteniendo respuestas pendientes de confirmación");
        
        List<RespuestaReclamacionDTO> respuestasPendientes = respuestaService.obtenerRespuestasPendientesConfirmacion();
        
        return ResponseEntity.ok(respuestasPendientes);
    }

    // =================== CONTADORES Y ESTADÍSTICAS ===================

    @Operation(
        summary = "Contar respuestas por reclamación",
        description = "Obtiene la cantidad total de respuestas de una reclamación específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/reclamacion/{reclamacionId}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> contarRespuestasPorReclamacion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        long cantidad = respuestaService.contarRespuestasPorReclamacion(reclamacionId);
        
        return ResponseEntity.ok(Map.of(
            "reclamacionId", reclamacionId,
            "cantidad", cantidad
        ));
    }

    @Operation(
        summary = "Contar respuestas por tipo",
        description = "Obtiene la cantidad de respuestas por tipo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/tipo/{tipoRespuesta}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Serializable>> contarRespuestasPorTipo(
            @Parameter(description = "Tipo de respuesta", required = true)
            @PathVariable TipoRespuesta tipoRespuesta) {
        
        long cantidad = respuestaService.contarRespuestasPorTipo(tipoRespuesta);
        
        return ResponseEntity.ok(Map.of(
            "tipo", tipoRespuesta.name(),
            "cantidad", cantidad
        ));
    }

    @Operation(
        summary = "Contar respuestas por usuario",
        description = "Obtiene la cantidad de respuestas creadas por un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/usuario/{usuarioResponsable}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Serializable>> contarRespuestasPorUsuario(
            @Parameter(description = "Usuario responsable", required = true, example = "juan.perez")
            @PathVariable @NotBlank String usuarioResponsable) {
        
        long cantidad = respuestaService.contarRespuestasPorUsuario(usuarioResponsable);
        
        return ResponseEntity.ok(Map.of(
            "usuario", usuarioResponsable,
            "cantidad", cantidad
        ));
    }

    @Operation(
        summary = "Contar respuestas pendientes de confirmación",
        description = "Obtiene la cantidad total de respuestas pendientes de confirmación"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/pendientes-confirmacion")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> contarRespuestasPendientesConfirmacion() {
        
        long cantidad = respuestaService.contarRespuestasPendientesConfirmacion();
        
        return ResponseEntity.ok(Map.of("pendientesConfirmacion", cantidad));
    }

    // =================== VALIDACIONES Y VERIFICACIONES ===================

    @Operation(
        summary = "Verificar si tiene respuesta final",
        description = "Verifica si una reclamación tiene al menos una respuesta marcada como final"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación completada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/tiene-final")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Boolean>> tieneRespuestaFinal(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        boolean tieneFinal = respuestaService.tieneRespuestaFinal(reclamacionId);
        
        return ResponseEntity.ok(Map.of("tieneFinal", tieneFinal));
    }

    @Operation(
        summary = "Verificar si tiene respuestas",
        description = "Verifica si una reclamación tiene al menos una respuesta"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación completada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/tiene-respuestas")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Boolean>> tieneRespuestas(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        boolean tieneRespuestas = respuestaService.tieneRespuestas(reclamacionId);
        
        return ResponseEntity.ok(Map.of("tieneRespuestas", tieneRespuestas));
    }

    @Operation(
        summary = "Verificar si se puede crear respuesta",
        description = "Verifica si se puede crear una nueva respuesta para la reclamación"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación completada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/puede-crear-respuesta")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Boolean>> puedeCrearRespuesta(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        boolean puedeCrear = respuestaService.puedeCrearRespuesta(reclamacionId);
        
        return ResponseEntity.ok(Map.of("puedeCrearRespuesta", puedeCrear));
    }

    // =================== CÁLCULOS DE TIEMPO ===================

    @Operation(
        summary = "Calcular tiempo de respuesta",
        description = "Calcula el tiempo transcurrido desde la reclamación hasta la primera respuesta en horas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cálculo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada o sin respuestas",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/tiempo-respuesta")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> calcularTiempoRespuesta(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        long tiempoHoras = respuestaService.calcularTiempoRespuestaEnHoras(reclamacionId);
        
        return ResponseEntity.ok(Map.of(
            "reclamacionId", reclamacionId,
            "tiempoRespuestaHoras", tiempoHoras
        ));
    }

    @Operation(
        summary = "Calcular tiempo de resolución",
        description = "Calcula el tiempo transcurrido desde la reclamación hasta la respuesta final en horas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cálculo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada o sin respuesta final",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamacion/{reclamacionId}/tiempo-resolucion")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> calcularTiempoResolucion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long reclamacionId) {
        
        long tiempoHoras = respuestaService.calcularTiempoResolucionEnHoras(reclamacionId);
        
        return ResponseEntity.ok(Map.of(
            "reclamacionId", reclamacionId,
            "tiempoResolucionHoras", tiempoHoras
        ));
    }
}
