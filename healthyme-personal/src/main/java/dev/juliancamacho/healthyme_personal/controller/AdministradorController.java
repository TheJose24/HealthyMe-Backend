package dev.juliancamacho.healthyme_personal.controller;


import dev.juliancamacho.healthyme_personal.dto.AdministradorDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.AdministradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
@RequiredArgsConstructor
@Tag(name = "Administradors",
        description = "API para gestionar administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    // CREATE
    @Operation(
            summary = "Crear un administrador",
            description = "Registra un nuevo administrador en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Administrador creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping()
    public ResponseEntity<AdministradorDto> createAdministrador(@Valid @RequestBody AdministradorDto administradorDto) {
        return new ResponseEntity<>(administradorService.createAdministrador(administradorDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @Operation(
            summary = "Obtener administrador por ID",
            description = "Retorna una lista de todos los administradores segun su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
                    @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDto> getAdministradorById(@PathVariable Integer id) {
        return new ResponseEntity<>(administradorService.getAdministradorById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @Operation(
            summary = "Obtener todos los administrador",
            description = "Retorna una lista de todos los administradores registrados en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de administradores obtenida exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay administradores registradas")
            }
    )
    @GetMapping()
    public ResponseEntity<List<AdministradorDto>> getAllAdministrador() {
        List<AdministradorDto> administrador = administradorService.getAllAdministrador();
        if (administrador.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay datos
        }
        return new ResponseEntity<>(administrador, HttpStatus.OK);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar una administrador",
            description = "Actualiza los datos de una administrador existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Administrador actualizadao exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Administrador no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDto> updateAdministrador(@PathVariable Integer id, @Valid @RequestBody AdministradorDto administradorDto) {
        return new ResponseEntity<>(administradorService.updateAdministrador(id, administradorDto), HttpStatus.OK);
    }

    // DELETE
    @Operation(
            summary = "Eliminar una administrador",
            description = "Elimina una administrador existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Administrador eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdministradorById(@PathVariable Integer id) {
        administradorService.deleteAdministradorById(id);
        return ResponseEntity.ok("Administrador eliminado con éxito");
    }
}

