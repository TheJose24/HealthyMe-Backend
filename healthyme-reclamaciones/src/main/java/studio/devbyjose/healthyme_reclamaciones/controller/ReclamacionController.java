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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.ReclamacionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.lang.constant.Constable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de reclamaciones
 * Proporciona endpoints para el CRUD y operaciones de negocio de reclamaciones
 */
@Tag(
    name = "Reclamaciones", 
    description = "API para la gestión integral de reclamaciones del sistema de salud"
)
@Slf4j
@RestController
@RequestMapping("/api/v1/reclamaciones")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReclamacionController {

    private final ReclamacionService reclamacionService;

    // =================== OPERACIONES CRUD ===================

    @Operation(
        summary = "Crear nueva reclamación",
        description = "Crea una nueva reclamación en el sistema con numeración automática y asignación inicial"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Reclamación creada exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto - Datos duplicados",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ReclamacionDTO> crearReclamacion(
            @Parameter(description = "Datos para crear la reclamación", required = true)
            @Valid @RequestBody CreateReclamacionDTO createDTO) {
        
        log.info("Creando nueva reclamación para paciente: {}", createDTO.getNombreReclamante());
        
        ReclamacionDTO reclamacionCreada = reclamacionService.crearReclamacion(createDTO);
        
        log.info("Reclamación creada exitosamente: {}", reclamacionCreada.getNumeroReclamacion());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(reclamacionCreada);
    }

    @Operation(
        summary = "Obtener reclamación por ID",
        description = "Recupera una reclamación específica utilizando su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación encontrada",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<ReclamacionDTO> obtenerPorId(
            @Parameter(description = "ID único de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id) {
        
        log.debug("Obteniendo reclamación por ID: {}", id);
        
        ReclamacionDTO reclamacion = reclamacionService.obtenerPorId(id);
        
        return ResponseEntity.ok(reclamacion);
    }

    @Operation(
        summary = "Obtener reclamación por número",
        description = "Recupera una reclamación utilizando su número único generado automáticamente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación encontrada",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/numero/{numeroReclamacion}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<ReclamacionDTO> obtenerPorNumero(
            @Parameter(description = "Número único de la reclamación", required = true, example = "REC-2024-000001")
            @PathVariable @NotBlank String numeroReclamacion) {
        
        log.debug("Obteniendo reclamación por número: {}", numeroReclamacion);
        
        ReclamacionDTO reclamacion = reclamacionService.obtenerPorNumero(numeroReclamacion);
        
        return ResponseEntity.ok(reclamacion);
    }

    @Operation(
        summary = "Actualizar reclamación",
        description = "Actualiza los datos de una reclamación existente. Solo se pueden modificar campos específicos según el estado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
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
            description = "Conflicto - Estado no permite modificación",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> actualizarReclamacion(
            @Parameter(description = "ID único de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            @Parameter(description = "Datos de actualización", required = true)
            @Valid @RequestBody UpdateReclamacionDTO updateDTO) {
        
        log.info("Actualizando reclamación ID: {}", id);
        
        ReclamacionDTO reclamacionActualizada = reclamacionService.actualizarReclamacion(id, updateDTO);
        
        log.info("Reclamación actualizada exitosamente: {}", reclamacionActualizada.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionActualizada);
    }

    @Operation(
        summary = "Eliminar reclamación",
        description = "Realiza eliminación lógica de una reclamación cambiando su estado a ANULADO"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Reclamación eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto - No se puede eliminar en el estado actual",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> eliminarReclamacion(
            @Parameter(description = "ID único de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id) {
        
        log.info("Eliminando reclamación ID: {}", id);
        
        reclamacionService.eliminarReclamacion(id);
        
        log.info("Reclamación eliminada exitosamente: {}", id);
        
        return ResponseEntity.noContent().build();
    }

    // =================== CONSULTAS PAGINADAS ===================

    @Operation(
        summary = "Listar todas las reclamaciones",
        description = "Obtiene un listado paginado de todas las reclamaciones activas del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de reclamaciones obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<ReclamacionDTO>> obtenerTodas(
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaReclamacion") Pageable pageable) {
        
        log.debug("Obteniendo todas las reclamaciones - Página: {}, Tamaño: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ReclamacionDTO> reclamaciones = reclamacionService.obtenerTodas(pageable);
        
        return ResponseEntity.ok(reclamaciones);
    }

    @Operation(
        summary = "Buscar reclamaciones con filtros",
        description = "Búsqueda avanzada de reclamaciones utilizando múltiples criterios de filtrado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Búsqueda completada exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetros de búsqueda inválidos",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/buscar")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<ReclamacionDTO>> buscarConFiltros(
            @Parameter(description = "Estado de la reclamación")
            @RequestParam(required = false) EstadoReclamacion estado,
            
            @Parameter(description = "Prioridad de la reclamación")
            @RequestParam(required = false) PrioridadReclamacion prioridad,
            
            @Parameter(description = "Tipo de motivo de la reclamación")
            @RequestParam(required = false) TipoMotivo tipoMotivo,
            
            @Parameter(description = "Fecha de inicio del rango de búsqueda", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            
            @Parameter(description = "Fecha de fin del rango de búsqueda", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            
            @Parameter(description = "Usuario asignado a la reclamación")
            @RequestParam(required = false) String asignadoA,
            
            @Parameter(description = "Área responsable de la reclamación")
            @RequestParam(required = false) String areaResponsable,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaReclamacion") Pageable pageable) {
        
        log.debug("Buscando reclamaciones con filtros - Estado: {}, Prioridad: {}, Usuario: {}", 
                 estado, prioridad, asignadoA);
        
        Page<ReclamacionDTO> reclamaciones = reclamacionService.buscarConFiltros(
                estado, prioridad, tipoMotivo, fechaDesde, fechaHasta, 
                asignadoA, areaResponsable, pageable);
        
        return ResponseEntity.ok(reclamaciones);
    }

    @Operation(
        summary = "Búsqueda por texto libre",
        description = "Busca reclamaciones por texto en descripción y detalles del incidente"
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
    @GetMapping("/buscar/texto")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<ReclamacionDTO>> buscarPorTexto(
            @Parameter(description = "Texto a buscar en descripción y detalles", required = true, example = "problema con cita")
            @RequestParam @NotBlank String textoBusqueda,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaReclamacion") Pageable pageable) {
        
        log.debug("Buscando reclamaciones por texto: {}", textoBusqueda);
        
        Page<ReclamacionDTO> reclamaciones = reclamacionService.buscarPorTexto(textoBusqueda, pageable);
        
        return ResponseEntity.ok(reclamaciones);
    }

    // =================== CONSULTAS POR RELACIONES ===================

    @Operation(
        summary = "Obtener reclamaciones por paciente",
        description = "Recupera todas las reclamaciones asociadas a un paciente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamaciones del paciente obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Paciente no encontrado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/paciente/{idPaciente}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Page<ReclamacionDTO>> obtenerPorPaciente(
            @Parameter(description = "ID del paciente", required = true, example = "12345")
            @PathVariable @Positive Long idPaciente,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 10, sort = "fechaReclamacion") Pageable pageable) {
        
        log.debug("Obteniendo reclamaciones del paciente: {}", idPaciente);
        
        Page<ReclamacionDTO> reclamaciones = reclamacionService.obtenerPorPaciente(idPaciente, pageable);
        
        return ResponseEntity.ok(reclamaciones);
    }

    @Operation(
        summary = "Obtener reclamaciones por cita médica",
        description = "Recupera todas las reclamaciones relacionadas con una cita médica específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamaciones de la cita obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/cita/{idCita}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<List<ReclamacionDTO>> obtenerPorCita(
            @Parameter(description = "ID de la cita médica", required = true, example = "67890")
            @PathVariable @Positive Long idCita) {
        
        log.debug("Obteniendo reclamaciones de la cita: {}", idCita);
        
        List<ReclamacionDTO> reclamaciones = reclamacionService.obtenerPorCita(idCita);
        
        return ResponseEntity.ok(reclamaciones);
    }

    @Operation(
        summary = "Obtener reclamaciones por pago",
        description = "Recupera todas las reclamaciones relacionadas con un pago específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamaciones del pago obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/pago/{idPago}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<List<ReclamacionDTO>> obtenerPorPago(
            @Parameter(description = "ID del pago", required = true, example = "54321")
            @PathVariable @Positive Long idPago) {
        
        log.debug("Obteniendo reclamaciones del pago: {}", idPago);
        
        List<ReclamacionDTO> reclamaciones = reclamacionService.obtenerPorPago(idPago);
        
        return ResponseEntity.ok(reclamaciones);
    }

    // =================== GESTIÓN DE ESTADOS ===================

    @Operation(
        summary = "Cambiar estado de reclamación",
        description = "Cambia el estado de una reclamación y registra el seguimiento correspondiente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estado cambiado exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Cambio de estado inválido",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PatchMapping("/{id}/estado")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> cambiarEstado(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Nuevo estado", required = true)
            @RequestParam @NotNull EstadoReclamacion nuevoEstado,
            
            @Parameter(description = "Comentario del cambio de estado")
            @RequestParam(required = false) String comentario,
            
            @Parameter(description = "Usuario responsable del cambio", required = true)
            @RequestParam @NotBlank String usuarioResponsable) {
        
        log.info("Cambiando estado de reclamación {} a {}", id, nuevoEstado);
        
        ReclamacionDTO reclamacionActualizada = reclamacionService.cambiarEstado(
                id, nuevoEstado, comentario, usuarioResponsable);
        
        log.info("Estado cambiado exitosamente para reclamación: {}", 
                reclamacionActualizada.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionActualizada);
    }

    @Operation(
        summary = "Asignar reclamación",
        description = "Asigna una reclamación a un usuario específico y área responsable"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación asignada exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PatchMapping("/{id}/asignar")
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> asignarReclamacion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Usuario al que se asigna", required = true)
            @RequestParam @NotBlank String asignadoA,
            
            @Parameter(description = "Área responsable", required = true)
            @RequestParam @NotBlank String areaResponsable) {
        
        log.info("Asignando reclamación {} a usuario {} del área {}", id, asignadoA, areaResponsable);
        
        ReclamacionDTO reclamacionAsignada = reclamacionService.asignarReclamacion(
                id, asignadoA, areaResponsable);
        
        log.info("Reclamación asignada exitosamente: {}", 
                reclamacionAsignada.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionAsignada);
    }

    @Operation(
        summary = "Cambiar prioridad",
        description = "Modifica la prioridad de una reclamación con justificación"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Prioridad cambiada exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PatchMapping("/{id}/prioridad")
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> cambiarPrioridad(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Nueva prioridad", required = true)
            @RequestParam @NotNull PrioridadReclamacion nuevaPrioridad,
            
            @Parameter(description = "Justificación del cambio de prioridad")
            @RequestParam(required = false) String justificacion) {
        
        log.info("Cambiando prioridad de reclamación {} a {}", id, nuevaPrioridad);
        
        ReclamacionDTO reclamacionActualizada = reclamacionService.cambiarPrioridad(
                id, nuevaPrioridad, justificacion);
        
        log.info("Prioridad cambiada exitosamente para reclamación: {}", 
                reclamacionActualizada.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionActualizada);
    }

    // =================== CONSULTAS DE GESTIÓN ===================

    @Operation(
        summary = "Obtener reclamaciones vencidas",
        description = "Lista todas las reclamaciones que han superado su fecha límite de respuesta"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamaciones vencidas obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/vencidas")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<Page<ReclamacionDTO>> obtenerReclamacionesVencidas(
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaLimiteRespuesta") Pageable pageable) {
        
        log.debug("Obteniendo reclamaciones vencidas");
        
        Page<ReclamacionDTO> reclamacionesVencidas = reclamacionService.obtenerReclamacionesVencidas(pageable);
        
        return ResponseEntity.ok(reclamacionesVencidas);
    }

    @Operation(
        summary = "Obtener reclamaciones por vencer",
        description = "Lista las reclamaciones que están próximas a vencer según las horas de anticipación especificadas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamaciones por vencer obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/por-vencer")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<Page<ReclamacionDTO>> obtenerReclamacionesPorVencer(
            @Parameter(description = "Horas de anticipación para considerar 'por vencer'", example = "24")
            @RequestParam(defaultValue = "24") int horasAnticipacion,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaLimiteRespuesta") Pageable pageable) {
        
        log.debug("Obteniendo reclamaciones por vencer en {} horas", horasAnticipacion);
        
        Page<ReclamacionDTO> reclamacionesPorVencer = reclamacionService.obtenerReclamacionesPorVencer(
                horasAnticipacion, pageable);
        
        return ResponseEntity.ok(reclamacionesPorVencer);
    }

    @Operation(
        summary = "Obtener reclamaciones asignadas",
        description = "Lista las reclamaciones asignadas a un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamaciones asignadas obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/asignadas/{usuarioAsignado}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<Page<ReclamacionDTO>> obtenerReclamacionesAsignadas(
            @Parameter(description = "Usuario asignado", required = true, example = "juan.perez")
            @PathVariable @NotBlank String usuarioAsignado,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaReclamacion") Pageable pageable) {
        
        log.debug("Obteniendo reclamaciones asignadas a: {}", usuarioAsignado);
        
        Page<ReclamacionDTO> reclamacionesAsignadas = reclamacionService.obtenerReclamacionesAsignadas(
                usuarioAsignado, pageable);
        
        return ResponseEntity.ok(reclamacionesAsignadas);
    }

    @Operation(
        summary = "Obtener reclamaciones por área",
        description = "Lista las reclamaciones de un área responsable específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamaciones del área obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/area/{areaResponsable}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<Page<ReclamacionDTO>> obtenerReclamacionesPorArea(
            @Parameter(description = "Área responsable", required = true, example = "Atención al Cliente")
            @PathVariable @NotBlank String areaResponsable,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 20, sort = "fechaReclamacion") Pageable pageable) {
        
        log.debug("Obteniendo reclamaciones del área: {}", areaResponsable);
        
        Page<ReclamacionDTO> reclamacionesArea = reclamacionService.obtenerReclamacionesPorArea(
                areaResponsable, pageable);
        
        return ResponseEntity.ok(reclamacionesArea);
    }

    // =================== OPERACIONES DE NEGOCIO ===================

    @Operation(
        summary = "Cerrar reclamación",
        description = "Cierra una reclamación marcándola como resuelta con la solución aplicada"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación cerrada exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "No se puede cerrar en el estado actual",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/{id}/cerrar")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> cerrarReclamacion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Descripción de la solución aplicada", required = true)
            @RequestParam @NotBlank String solucionAplicada,
            
            @Parameter(description = "Usuario responsable del cierre", required = true)
            @RequestParam @NotBlank String usuarioResponsable) {
        
        log.info("Cerrando reclamación ID: {}", id);
        
        ReclamacionDTO reclamacionCerrada = reclamacionService.cerrarReclamacion(
                id, solucionAplicada, usuarioResponsable);
        
        log.info("Reclamación cerrada exitosamente: {}", 
                reclamacionCerrada.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionCerrada);
    }

    @Operation(
        summary = "Reabrir reclamación",
        description = "Reabre una reclamación previamente cerrada por motivos justificados"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación reabierta exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "No se puede reabrir en el estado actual",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/{id}/reabrir")
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> reabrirReclamacion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Motivo de la reapertura", required = true)
            @RequestParam @NotBlank String motivo,
            
            @Parameter(description = "Usuario responsable de la reapertura", required = true)
            @RequestParam @NotBlank String usuarioResponsable) {
        
        log.info("Reabriendo reclamación ID: {} por motivo: {}", id, motivo);
        
        ReclamacionDTO reclamacionReabierta = reclamacionService.reabrirReclamacion(
                id, motivo, usuarioResponsable);
        
        log.info("Reclamación reabierta exitosamente: {}", 
                reclamacionReabierta.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionReabierta);
    }

    @Operation(
        summary = "Escalar reclamación",
        description = "Escala una reclamación a un supervisor para atención especializada"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación escalada exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/{id}/escalar")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> escalarReclamacion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Motivo del escalamiento", required = true)
            @RequestParam @NotBlank String motivoEscalamiento,
            
            @Parameter(description = "Supervisor al que se escala", required = true)
            @RequestParam @NotBlank String supervisorAsignado) {
        
        log.info("Escalando reclamación ID: {} a supervisor: {}", id, supervisorAsignado);
        
        ReclamacionDTO reclamacionEscalada = reclamacionService.escalarReclamacion(
                id, motivoEscalamiento, supervisorAsignado);
        
        log.info("Reclamación escalada exitosamente: {}", 
                reclamacionEscalada.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionEscalada);
    }

    @Operation(
        summary = "Marcar como urgente",
        description = "Marca una reclamación como urgente cambiando su prioridad"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reclamación marcada como urgente exitosamente",
            content = @Content(schema = @Schema(implementation = ReclamacionDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/{id}/marcar-urgente")
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ReclamacionDTO> marcarComoUrgente(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id,
            
            @Parameter(description = "Justificación de la urgencia", required = true)
            @RequestParam @NotBlank String justificacion) {
        
        log.info("Marcando reclamación ID: {} como urgente", id);
        
        ReclamacionDTO reclamacionUrgente = reclamacionService.marcarComoUrgente(id, justificacion);
        
        log.info("Reclamación marcada como urgente: {}", 
                reclamacionUrgente.getNumeroReclamacion());
        
        return ResponseEntity.ok(reclamacionUrgente);
    }

    // =================== VALIDACIONES Y VERIFICACIONES ===================

    @Operation(
        summary = "Verificar existencia de reclamación",
        description = "Verifica si existe una reclamación con el ID especificado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación completada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/{id}/existe")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Boolean>> existeReclamacion(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id) {
        
        boolean existe = reclamacionService.existeReclamacion(id);
        
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @Operation(
        summary = "Verificar número de reclamación",
        description = "Verifica si un número de reclamación ya está en uso"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación completada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/numero/{numeroReclamacion}/existe")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Boolean>> existeNumeroReclamacion(
            @Parameter(description = "Número de reclamación", required = true, example = "REC-2024-000001")
            @PathVariable @NotBlank String numeroReclamacion) {
        
        boolean existe = reclamacionService.existeNumeroReclamacion(numeroReclamacion);
        
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @Operation(
        summary = "Verificar si está vencida",
        description = "Verifica si una reclamación ha superado su fecha límite de respuesta"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación completada",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Reclamación no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/{id}/vencida")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Boolean>> estaVencida(
            @Parameter(description = "ID de la reclamación", required = true, example = "1")
            @PathVariable @Positive Long id) {
        
        boolean vencida = reclamacionService.estaVencida(id);
        
        return ResponseEntity.ok(Map.of("vencida", vencida));
    }

    // =================== CONTADORES Y ESTADÍSTICAS BÁSICAS ===================

    @Operation(
        summary = "Contar por estado",
        description = "Obtiene la cantidad de reclamaciones en un estado específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/estado/{estado}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Constable>> contarPorEstado(
            @Parameter(description = "Estado a contar", required = true)
            @PathVariable EstadoReclamacion estado) {
        
        long cantidad = reclamacionService.contarPorEstado(estado);

        return ResponseEntity.ok(Map.of(
            "estado", estado,
            "cantidad", cantidad
        ));
    }

    @Operation(
        summary = "Contar reclamaciones vencidas",
        description = "Obtiene el total de reclamaciones que han superado su fecha límite"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/vencidas")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> contarReclamacionesVencidas() {
        
        long cantidad = reclamacionService.contarReclamacionesVencidas();
        
        return ResponseEntity.ok(Map.of("vencidas", cantidad));
    }

    @Operation(
        summary = "Contar por usuario asignado",
        description = "Obtiene la cantidad de reclamaciones asignadas a un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/usuario/{usuarioAsignado}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Serializable>> contarReclamacionesAsignadas(
            @Parameter(description = "Usuario asignado", required = true, example = "juan.perez")
            @PathVariable @NotBlank String usuarioAsignado) {
        
        long cantidad = reclamacionService.contarReclamacionesAsignadas(usuarioAsignado);
        
        return ResponseEntity.ok(Map.of(
            "usuario", usuarioAsignado,
            "cantidad", cantidad
        ));
    }

    @Operation(
        summary = "Contar por área",
        description = "Obtiene la cantidad de reclamaciones de un área responsable específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo completado",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/contar/area/{areaResponsable}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Serializable>> contarReclamacionesPorArea(
            @Parameter(description = "Área responsable", required = true, example = "Atención al Cliente")
            @PathVariable @NotBlank String areaResponsable) {
        
        long cantidad = reclamacionService.contarReclamacionesPorArea(areaResponsable);
        
        return ResponseEntity.ok(Map.of(
            "area", areaResponsable,
            "cantidad", cantidad
        ));
    }
}
