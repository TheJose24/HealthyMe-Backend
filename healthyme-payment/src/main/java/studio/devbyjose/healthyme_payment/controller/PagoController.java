package studio.devbyjose.healthyme_payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_commons.client.dto.CreatePagoDTO;
import studio.devbyjose.healthyme_commons.client.dto.PagoDTO;
import studio.devbyjose.healthyme_commons.dto.ErrorResponseDTO;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;
import studio.devbyjose.healthyme_payment.dto.PagoResponseDTO;
import studio.devbyjose.healthyme_payment.service.interfaces.PagoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    @Operation(summary = "Crear un nuevo pago", description = "Crea un nuevo registro de pago en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PagoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos del pago inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PagoDTO> createPago(
            @Valid @RequestBody CreatePagoDTO createPagoDTO) {
        PagoDTO resultado = pagoService.createPago(createPagoDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID", description = "Recupera un pago específico según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(schema = @Schema(implementation = PagoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PagoResponseDTO> getPagoById(
            @Parameter(description = "ID del pago a buscar", required = true)
            @PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.getPagoById(id));
    }

    @GetMapping
    public ResponseEntity<List<PagoDTO>> getPagosByEstado(
            @RequestParam(required = false) EstadoPago estado) {
        List<PagoDTO> pagos = pagoService.getPagosByEstado(estado);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<PagoDTO>> getPagosByPaciente(@PathVariable Integer idPaciente) {
        List<PagoDTO> pagos = pagoService.getPagosByPaciente(idPaciente);
        return ResponseEntity.ok(pagos);
    }

    @PostMapping("/{id}/procesar")
    public ResponseEntity<PagoResponseDTO> processPago(@PathVariable Integer id) {
        PagoResponseDTO resultado = pagoService.processPago(id);
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PagoDTO> updateEstadoPago(
            @PathVariable Integer id, 
            @RequestParam EstadoPago estado) {
        PagoDTO pagoActualizado = pagoService.updateEstadoPago(id, estado);
        return ResponseEntity.ok(pagoActualizado);
    }

    @GetMapping("/referencia")
    public ResponseEntity<PagoDTO> getPagoByReferencia(
            @RequestParam EntidadOrigen entidad,
            @RequestParam Integer idReferencia) {
        PagoDTO pago = pagoService.getPagoByReferencia(entidad, idReferencia);
        return ResponseEntity.ok(pago);
    }
}