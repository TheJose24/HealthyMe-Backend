package studio.devbyjose.security_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.security_service.dto.*;
import studio.devbyjose.security_service.enums.EstadoUsuario;
import studio.devbyjose.security_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UserController {

    private final UserService userService;

    // ---------------------- Endpoints para consulta de usuarios ----------------------

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Obtener detalles completos del usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserDetailsById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserDetailById(id));
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Obtener usuario por DNI")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    public ResponseEntity<UserDTO> getUserByDni(@PathVariable String dni) {
        return ResponseEntity.ok(userService.getUserByDni(dni));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Obtener usuario por nombre de usuario")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping
    @Operation(summary = "Buscar usuarios con filtros", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(search, rol, estado, pageable));
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de usuarios", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }

    @GetMapping("/by-role/{rolNombre}")
    @Operation(summary = "Obtener usuarios por rol", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String rolNombre) {
        List<UserDTO> usuarios = userService.getUsersByRole(rolNombre);
        return ResponseEntity.ok(usuarios);
    }

    // ---------------------- Endpoints para gestión de estado de usuarios ----------------------

    @PutMapping("/{id}/suspend")
    @Operation(summary = "Suspender usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> suspendUser(@PathVariable Integer id) {
        userService.changeUserStatus(id, String.valueOf(EstadoUsuario.SUSPENDIDO));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activar usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Integer id) {
        userService.changeUserStatus(id, EstadoUsuario.ACTIVO.name());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.changeUserStatus(id, EstadoUsuario.ELIMINADO.name());
        return ResponseEntity.noContent().build();
    }

    // ---------------------- Endpoints para actualización de usuario ----------------------

    @PutMapping("/{id}/password")
    @Operation(summary = "Actualizar contraseña", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updatePassword(
            @PathVariable Integer id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.updateUserPassword(id, currentPassword, newPassword));
    }

    @PutMapping("/{id}/profile")
    @Operation(summary = "Actualizar perfil de usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateProfile(
            @PathVariable Integer id,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Actualizar rol de usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateRole(
            @PathVariable Integer id,
            @RequestParam String rolNombre) {
        return ResponseEntity.ok(userService.updateUserRole(id, rolNombre));
    }

    // ---------------------- Endpoints para gestión de contratos ----------------------

    @GetMapping("/{id}/contracts")
    @Operation(summary = "Obtener contratos de un usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContratoDTO>> getUserContracts(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserContracts(id));
    }

    @PostMapping("/{id}/contracts")
    @Operation(summary = "Crear contrato para un usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContratoDTO> createContract(
            @PathVariable Integer id,
            @Valid @RequestBody ContratoDTO contratoDTO) {
        ContratoDTO created = userService.createContract(id, contratoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/contracts/{contratoId}")
    @Operation(summary = "Actualizar contrato existente", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContratoDTO> updateContract(
            @PathVariable Integer contratoId,
            @Valid @RequestBody ContratoDTO contratoDTO) {
        return ResponseEntity.ok(userService.updateContract(contratoId, contratoDTO));
    }

    @PutMapping("/contracts/{contratoId}/terminate")
    @Operation(summary = "Terminar contrato anticipadamente", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContratoDTO> terminateContract(@PathVariable Integer contratoId) {
        return ResponseEntity.ok(userService.terminateContract(contratoId));
    }

    // ---------------------- Endpoint para usuario actual ----------------------

    @GetMapping("/me")
    @Operation(summary = "Obtener información del usuario actual", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}