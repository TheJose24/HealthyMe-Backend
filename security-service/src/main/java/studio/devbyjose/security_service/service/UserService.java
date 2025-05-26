package studio.devbyjose.security_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;
import studio.devbyjose.security_service.dto.*;
import studio.devbyjose.security_service.entity.Contrato;
import studio.devbyjose.security_service.entity.Persona;
import studio.devbyjose.security_service.entity.Rol;
import studio.devbyjose.security_service.entity.Usuario;
import studio.devbyjose.security_service.enums.EstadoUsuario;
import studio.devbyjose.security_service.exception.DuplicateUserException;
import studio.devbyjose.security_service.exception.InvalidOperationException;
import studio.devbyjose.security_service.mapper.ContratoMapper;
import studio.devbyjose.security_service.mapper.PersonaMapper;
import studio.devbyjose.security_service.mapper.UserMapper;
import studio.devbyjose.security_service.repository.ContratoRepository;
import studio.devbyjose.security_service.repository.PersonaRepository;
import studio.devbyjose.security_service.repository.RolRepository;
import studio.devbyjose.security_service.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de usuarios, perfiles y contratos del sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final ContratoRepository contratoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PersonaMapper personaMapper;
    private final ContratoMapper contratoMapper;
    private final AuthService authService;

    /**
     * Obtiene un usuario por su ID con datos básicos.
     */
    public UserDTO getUserById(Integer id) {
        Usuario usuario = findUserById(id);
        return userMapper.toDto(usuario);
    }

    /**
     * Obtiene un usuario por su ID con datos completos (persona y contratos).
     */
    public UserDTO getUserDetailById(Integer id) {
        Usuario usuario = findUserById(id);
        return userMapper.toDtoComplete(usuario);
    }

    /**
     * Obtiene un usuario por su DNI.
     */
    public UserDTO getUserByDni(String dni) {
        Usuario usuario = usuarioRepository.findByPersonaDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con DNI: " + dni, HttpStatus.NOT_FOUND));

        return userMapper.toDtoComplete(usuario);
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     */
    public UserDTO getUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado: " + username, HttpStatus.NOT_FOUND));

        return userMapper.toDtoComplete(usuario);
    }

    /**
     * Busca usuarios con paginación y filtrado.
     *
     * @param search Término de búsqueda (nombre, apellido, username)
     * @param rol Filtro por rol
     * @param estado Filtro por estado
     * @param pageable Configuración de paginación
     * @return Página con usuarios que coinciden con los criterios
     */
    public Page<UserDTO> searchUsers(String search, String rol, String estado, Pageable pageable) {
        // Crear especificaciones para los filtros
        Specification<Usuario> spec = Specification.where(null);

        if (StringUtils.hasText(search)) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("nombreUsuario")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("persona").get("nombre")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("persona").get("apellido")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("persona").get("dni")), "%" + search.toLowerCase() + "%")
                    )
            );
        }

        if (StringUtils.hasText(rol)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("rol").get("nombreRol"), rol)
            );
        }

        if (StringUtils.hasText(estado)) {
            try {
                EstadoUsuario estadoEnum = EstadoUsuario.valueOf(estado);
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("estado"), estadoEnum)
                );
            } catch (IllegalArgumentException e) {
                log.warn("Estado inválido en búsqueda: {}", estado);
            }
        }

        Page<Usuario> users = usuarioRepository.findAll(spec, pageable);
        return users.map(userMapper::toDto);
    }

    /**
     * Cambia el estado de un usuario (activar, suspender, eliminar).
     */
    @Transactional
    public void changeUserStatus(Integer id, String estado) {
        Usuario usuario = findUserById(id);

        try {
            EstadoUsuario estadoUsuario = EstadoUsuario.valueOf(estado);

            // No permitir reactivar usuarios eliminados
            if (usuario.getEstado() == EstadoUsuario.ELIMINADO && estadoUsuario != EstadoUsuario.ELIMINADO) {
                throw new InvalidOperationException("No se puede cambiar el estado de un usuario eliminado");
            }

            // Si se está eliminando, revocar todos sus tokens
            if (estadoUsuario == EstadoUsuario.ELIMINADO) {
                authService.logout(usuario.getNombreUsuario());
            }

            usuario.setEstado(estadoUsuario);
            usuarioRepository.save(usuario);
            log.info("Usuario con ID: {} cambió su estado a: {}", id, estado);
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException("Estado de usuario inválido: " + estado);
        }
    }

    /**
     * Actualiza la contraseña de un usuario verificando la contraseña actual.
     */
    @Transactional
    public UserDTO updateUserPassword(Integer id, String currentPassword, String newPassword) {
        Usuario usuario = findUserById(id);

        // Verificar acceso (solo el propio usuario o un admin pueden cambiar la contraseña)
        verifyAccess(usuario.getNombreUsuario());

        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, usuario.getContrasena())) {
            throw new InvalidOperationException("La contraseña actual es incorrecta");
        }

        // Verificar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(newPassword, usuario.getContrasena())) {
            throw new InvalidOperationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Actualizar contraseña
        usuario.setContrasena(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
        log.info("Contraseña actualizada para usuario con ID: {}", id);

        return userMapper.toDto(usuario);
    }

    /**
     * Actualiza el perfil de usuario (datos personales).
     */
    @Transactional
    public UserDTO updateUserProfile(Integer id, UserProfileUpdateRequest request) {
        Usuario usuario = findUserById(id);

        // Verificar acceso (solo el propio usuario o un admin pueden actualizar el perfil)
        verifyAccess(usuario.getNombreUsuario());

        // Actualizar datos de la persona
        Persona persona = usuario.getPersona();

        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(persona.getEmail())) {
            // Verificar que el email no esté en uso
            if (personaRepository.existsByEmailAndDniNot(request.getEmail(), persona.getDni())) {
                throw new DuplicateUserException("El email ya está en uso por otro usuario");
            }
            persona.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getTelefono())) {
            persona.setTelefono(request.getTelefono());
        }

        if (StringUtils.hasText(request.getDireccion())) {
            persona.setDireccion(request.getDireccion());
        }

        personaRepository.save(persona);

        // Actualizar imagen de perfil si se proporciona
        if (StringUtils.hasText(request.getImagenPerfil())) {
            usuario.setImagenPerfil(request.getImagenPerfil());
            usuarioRepository.save(usuario);
        }

        log.info("Perfil actualizado para usuario con ID: {}", id);
        return userMapper.toDtoComplete(usuario);
    }

    /**
     * Actualiza el rol de un usuario (solo administradores).
     */
    @Transactional
    public UserDTO updateUserRole(Integer id, String rolNombre) {
        Usuario usuario = findUserById(id);

        // Solo los administradores pueden cambiar roles
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Rol.ROLE_ADMIN));

        if (!isAdmin) {
            throw new AccessDeniedException("Se requieren permisos de administrador para cambiar roles");
        }

        // Buscar el rol
        Rol nuevoRol = rolRepository.findByNombreRol(rolNombre)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol no encontrado: " + rolNombre, HttpStatus.NOT_FOUND));

        // Actualizar el rol
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);

        log.info("Rol actualizado a {} para usuario con ID: {}", rolNombre, id);
        return userMapper.toDto(usuario);
    }

    /**
     * Crea un nuevo contrato para un usuario.
     */
    @Transactional
    public ContratoDTO createContract(Integer userId, ContratoDTO contratoDTO) {
        Usuario usuario = findUserById(userId);

        // Verificar que el rol permita contratos
        if (usuario.getRol().getNombreRol().equals(Rol.ROLE_PACIENTE)) {
            throw new InvalidOperationException("No se pueden crear contratos para pacientes");
        }

        // Validar fechas del contrato
        if (contratoDTO.getFechaInicio().isAfter(contratoDTO.getFechaFin())) {
            throw new InvalidOperationException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // Crear contrato
        Contrato contrato = contratoMapper.toEntity(contratoDTO);
        contrato.setUsuario(usuario);

        contratoRepository.save(contrato);
        log.info("Contrato creado para usuario con ID: {}", userId);

        return contratoMapper.toDto(contrato);
    }

    /**
     * Actualiza un contrato existente.
     */
    @Transactional
    public ContratoDTO updateContract(Integer contratoId, ContratoDTO contratoDTO) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrato no encontrado con ID: " + contratoId, HttpStatus.NOT_FOUND));

        // Validar fechas del contrato
        if (contratoDTO.getFechaInicio().isAfter(contratoDTO.getFechaFin())) {
            throw new InvalidOperationException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // Actualizar campos
        contrato.setFechaInicio(contratoDTO.getFechaInicio());
        contrato.setFechaFin(contratoDTO.getFechaFin());

        // Actualizar otros campos si se implementan en el DTO
        if (contratoDTO instanceof ContratoDetalleDTO) {
            ContratoDetalleDTO detalleDTO = (ContratoDetalleDTO) contratoDTO;
            if (detalleDTO.getSalario() != null) {
                contrato.setSalario(detalleDTO.getSalario());
            }
            if (StringUtils.hasText(detalleDTO.getDescripcion())) {
                contrato.setDescripcion(detalleDTO.getDescripcion());
            }
            if (StringUtils.hasText(detalleDTO.getCargo())) {
                contrato.setCargo(detalleDTO.getCargo());
            }
            if (detalleDTO.getHorasSemana() != null) {
                contrato.setHorasSemana(detalleDTO.getHorasSemana());
            }
        }

        contratoRepository.save(contrato);
        log.info("Contrato actualizado con ID: {}", contratoId);

        return contratoMapper.toDto(contrato);
    }

    /**
     * Obtiene todos los contratos de un usuario.
     */
    public List<ContratoDTO> getUserContracts(Integer userId) {
        Usuario usuario = findUserById(userId);
        return usuario.getContratos().stream()
                .map(contratoMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Termina un contrato anticipadamente (establece la fecha fin en hoy).
     */
    @Transactional
    public ContratoDTO terminateContract(Integer contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrato no encontrado con ID: " + contratoId, HttpStatus.NOT_FOUND));

        // Verificar que el contrato esté activo
        if (!contrato.isActivo()) {
            throw new InvalidOperationException("El contrato no está activo");
        }

        // Establecer fecha fin en hoy
        contrato.setFechaFin(LocalDate.now());
        contratoRepository.save(contrato);

        log.info("Contrato terminado con ID: {}", contratoId);
        return contratoMapper.toDto(contrato);
    }

    /**
     * Obtiene estadísticas generales de usuarios.
     */
    public UserStatsDTO getUserStats() {
        // Solo administradores pueden ver estadísticas
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Rol.ROLE_ADMIN));

        if (!isAdmin) {
            throw new AccessDeniedException("Se requieren permisos de administrador para ver estadísticas");
        }

        long totalUsers = usuarioRepository.count();
        long activeUsers = usuarioRepository.countByEstado(EstadoUsuario.ACTIVO);
        long suspendedUsers = usuarioRepository.countByEstado(EstadoUsuario.SUSPENDIDO);

        // Contar usuarios por rol
        List<Rol> roles = rolRepository.findAll();

        return UserStatsDTO.builder()
                .totalUsuarios(totalUsers)
                .usuariosActivos(activeUsers)
                .usuariosSuspendidos(suspendedUsers)
                .usuariosEliminados(totalUsers - activeUsers - suspendedUsers)
                .build();
    }

    /**
     * Busca un usuario por ID y verifica que no esté eliminado.
     */
    private Usuario findUserById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + id, HttpStatus.NOT_FOUND));

        if (usuario.getEstado() == EstadoUsuario.ELIMINADO) {
            throw new ResourceNotFoundException(
                    "Usuario no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        return usuario;
    }

    /**
     * Verifica si el usuario actual tiene acceso a modificar los datos del usuario objetivo.
     */
    private void verifyAccess(String targetUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Rol.ROLE_ADMIN));

        // Solo el propio usuario o un admin pueden acceder
        if (!currentUsername.equals(targetUsername) && !isAdmin) {
            throw new AccessDeniedException("No tiene permisos para modificar este usuario");
        }
    }

    /**
     * Obtiene usuarios activos por rol.
     */
    public List<UserDTO> getUsersByRole(String rolNombre) {
        List<Usuario> usuarios = usuarioRepository.findByRolNombreAndEstado(rolNombre, EstadoUsuario.ACTIVO);
        return usuarios.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}