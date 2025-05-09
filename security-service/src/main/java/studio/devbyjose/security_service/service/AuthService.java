package studio.devbyjose.security_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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
import studio.devbyjose.security_service.exception.InvalidTokenException;
import studio.devbyjose.security_service.mapper.ContratoMapper;
import studio.devbyjose.security_service.mapper.PersonaMapper;
import studio.devbyjose.security_service.mapper.RolMapper;
import studio.devbyjose.security_service.mapper.UserMapper;
import studio.devbyjose.security_service.repository.ContratoRepository;
import studio.devbyjose.security_service.repository.PersonaRepository;
import studio.devbyjose.security_service.repository.RolRepository;
import studio.devbyjose.security_service.repository.UsuarioRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio responsable de la autenticación de usuarios, emisión de tokens JWT y operaciones relacionadas
 * con el registro y gestión de credenciales.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String DEFAULT_CLIENT_ID = "healthyme-web";
    private static final String DEFAULT_PROFILE_IMAGE = "default-profile.png";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final AuthorizationGrantType PASSWORD_GRANT = new AuthorizationGrantType(OAuth2ParameterNames.PASSWORD);

    private final AuthenticationManager authenticationManager;
    private final RegisteredClientRepository clientRepository;
    private final OAuth2AuthorizationService authorizationService;
    private final PasswordEncoder passwordEncoder;

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final ContratoRepository contratoRepository;

    private final OAuth2TokenGenerator<OAuth2Token> tokenGenerator;
    private final JwtDecoder jwtDecoder;

    private final UserMapper userMapper;
    private final PersonaMapper personaMapper;
    private final RolMapper rolMapper;
    private final ContratoMapper contratoMapper;

    /**
     * Registra un nuevo usuario en el sistema con validaciones mejoradas.
     *
     * @param request Datos de registro del usuario
     * @return AuthResponse con tokens de autenticación
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        log.info("Iniciando registro de usuario: {}", request.getNombreUsuario());

        // Validaciones de entrada mejoradas
        validateRegistrationRequest(request);

        try {
            // Crear y guardar la persona
            Persona persona = personaMapper.toEntity(request);
            personaRepository.save(persona);
            log.debug("Persona creada con DNI: {}", persona.getDni());

            // Determinar el rol según la solicitud o usar el predeterminado
            Rol rol = determineUserRole(request.getRolSolicitado());
            log.debug("Rol asignado: {}", rol.getNombreRol());

            // Crear y configurar el usuario
            Usuario usuario = buildUsuarioFromRequest(request, persona, rol);

            // Si se requiere un contrato (para roles médicos/personal), crearlo
            if (requiresContract(rol.getNombreRol()) &&
                    request.getFechaInicioContrato() != null &&
                    request.getFechaFinContrato() != null) {

                Contrato contrato = buildContratoFromRequest(request, rol);
                usuario.addContrato(contrato);

                contratoRepository.save(contrato);
                log.debug("Contrato creado para usuario con cargo: {}", contrato.getCargo());
            }

            // Guardar el usuario
            usuarioRepository.save(usuario);
            log.info("Usuario registrado exitosamente: {}", usuario.getNombreUsuario());

            // Registrar actividad
            auditUserRegistration(usuario);

            // Autenticar al usuario y generar tokens
            return login(new LoginRequest(request.getNombreUsuario(), request.getContrasena()));
        } catch (DuplicateUserException | InvalidOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error no controlado durante el registro de usuario", e);
            throw new InvalidOperationException("Error durante el registro: " + e.getMessage());
        }
    }

    /**
     * Autentica a un usuario y genera tokens de acceso con seguimiento de intentos de login.
     *
     * @param request Credenciales del usuario
     * @return AuthResponse con tokens de autenticación
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Procesando solicitud de login para usuario: {}", request.getNombreUsuario());

        // Verificar si el usuario existe antes de intentar autenticar
        Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow(() -> {
                    log.warn("Intento de login con usuario inexistente: {}", request.getNombreUsuario());
                    return new InvalidOperationException("Credenciales inválidas");
                });

        // Verificar si la cuenta está bloqueada por intentos fallidos
        checkAccountLockStatus(usuario);

        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticateUser(request.getNombreUsuario(), request.getContrasena());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Actualizar estado del usuario tras login exitoso
            usuario.registrarLoginExitoso();
            usuarioRepository.save(usuario);
            log.debug("Login exitoso registrado para usuario: {}", usuario.getNombreUsuario());

            // Obtener cliente OAuth2 y generar tokens
            RegisteredClient client = getRegisteredClient(DEFAULT_CLIENT_ID);
            OAuth2AccessToken accessToken = generateAccessToken(authentication, client, usuario);
            OAuth2RefreshToken refreshToken = generateRefreshToken(authentication, client);
            saveTokenAuthorization(authentication.getName(), accessToken, refreshToken, client.getId(), usuario);

            log.info("Login exitoso para usuario: {}", request.getNombreUsuario());
            return userMapper.toAuthResponse(usuario, accessToken, refreshToken);

        } catch (BadCredentialsException e) {
            // Registrar intento fallido e incrementar contador
            handleFailedLoginAttempt(usuario);
            throw new InvalidOperationException("Credenciales inválidas");
        } catch (Exception e) {
            log.error("Error durante autenticación", e);
            throw new InvalidOperationException("Error durante la autenticación: " + e.getMessage());
        }
    }

    /**
     * Refresca un token de acceso con validaciones mejoradas.
     *
     * @param request RefreshTokenRequest con el token de actualización
     * @return AuthResponse con nuevos tokens
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Procesando solicitud de refresh token");
        String refreshTokenValue = request.getRefreshToken();

        if (!StringUtils.hasText(refreshTokenValue)) {
            throw new InvalidTokenException("Token de actualización no proporcionado");
        }

        try {
            // Buscar la autorización basada en el refresh token
            OAuth2Authorization authorization = findAuthorizationByToken(refreshTokenValue);
            validateRefreshToken(authorization);

            // Cargar información del usuario
            String username = authorization.getPrincipalName();
            Usuario usuario = getUserByUsername(username);

            // Validaciones de seguridad
            validateUserForTokenRefresh(usuario);

            // Crear autenticación y generar nuevos tokens
            Authentication authentication = createAuthenticationFromUser(usuario);
            RegisteredClient client = getRegisteredClient(authorization.getRegisteredClientId());

            OAuth2AccessToken accessToken = generateAccessToken(authentication, client, usuario);
            OAuth2RefreshToken newRefreshToken = generateRefreshToken(authentication, client);

            // Revocar el token anterior y guardar la nueva autorización
            authorizationService.remove(authorization);
            saveTokenAuthorization(username, accessToken, newRefreshToken, client.getId(), usuario);

            log.info("Token refrescado exitosamente para usuario: {}", username);
            return userMapper.toAuthResponse(usuario, accessToken, newRefreshToken);

        } catch (InvalidTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al procesar refresh token", e);
            throw new InvalidTokenException("Error al procesar el token de actualización");
        }
    }

    /**
     * Cierra la sesión del usuario invalidando sus tokens activos.
     *
     * @param token Token de acceso a invalidar
     */
    @Transactional
    public void logout(String token) {
        try {
            String tokenValue = token.startsWith(TOKEN_PREFIX) ? token.substring(TOKEN_PREFIX.length()) : token;
            OAuth2Authorization authorization = authorizationService.findByToken(tokenValue, OAuth2TokenType.ACCESS_TOKEN);
            if (authorization != null) {
                authorizationService.remove(authorization);
                log.info("Access token revocado para usuario: {}", authorization.getPrincipalName());
            }

        } catch (Exception e) {
            throw new InvalidOperationException("Error al cerrar la sesión: " + e.getMessage());
        }
    }

    /**
     * Cambia la contraseña del usuario verificando la contraseña actual.
     *
     * @param username Usuario actual
     * @param currentPassword Contraseña actual
     * @param newPassword Nueva contraseña
     */
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        log.info("Procesando cambio de contraseña para usuario: {}", username);

        Usuario usuario = getUserByUsername(username);

        // Verificar la contraseña actual
        if (!passwordEncoder.matches(currentPassword, usuario.getContrasena())) {
            log.warn("Contraseña actual incorrecta para usuario: {}", username);
            throw new InvalidOperationException("Contraseña actual incorrecta");
        }

        // Comprobar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(newPassword, usuario.getContrasena())) {
            throw new InvalidOperationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Actualizar contraseña
        usuario.setContrasena(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        // Invalidar sesiones existentes para forzar un nuevo login
        logout(username);

        log.info("Contraseña actualizada correctamente para usuario: {}", username);
    }

    /**
     * Valida datos del registro verificando duplicidades y formato.
     */
    private void validateRegistrationRequest(RegisterRequest request) {
        // Validar nombre de usuario
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            log.warn("Intento de registro con nombre de usuario existente: {}", request.getNombreUsuario());
            throw new DuplicateUserException("El nombre de usuario ya está en uso");
        }

        // Validar DNI
        if (personaRepository.existsById(request.getDni())) {
            log.warn("Intento de registro con DNI existente: {}", request.getDni());
            throw new DuplicateUserException("El DNI ya está registrado en el sistema");
        }

        // Validar email si está presente
        if (StringUtils.hasText(request.getEmail()) && personaRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con email existente: {}", request.getEmail());
            throw new DuplicateUserException("El email ya está registrado en el sistema");
        }

        // Validar fechas de contrato si es personal médico
        if (StringUtils.hasText(request.getRolSolicitado()) &&
                !request.getRolSolicitado().equals(Rol.ROLE_PACIENTE)) {

            if (request.getFechaInicioContrato() == null || request.getFechaFinContrato() == null) {
                throw new InvalidOperationException("Las fechas de contrato son obligatorias para personal médico");
            }

            if (request.getFechaInicioContrato().isAfter(request.getFechaFinContrato())) {
                throw new InvalidOperationException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
        }
    }

    /**
     * Determina el rol del usuario basado en la solicitud o utiliza el predeterminado.
     */
    private Rol determineUserRole(String rolSolicitado) {
        Rol rol;
        if (!StringUtils.hasText(rolSolicitado)) {
            rol = getDefaultRole();
        } else {
            rol = rolRepository.findByNombreRol(rolSolicitado)
                    .orElseGet(this::getDefaultRole);
        }

        RolDTO rolDTO = rolMapper.toDto(rol);
        log.debug("Rol asignado: {} (ID: {})", rolDTO.getNombreRol(), rolDTO.getIdRol());

        return rol;
    }

    /**
     * Obtiene el rol predeterminado, creándolo si no existe.
     */
    private Rol getDefaultRole() {
        return rolRepository.findByNombreRol(Rol.ROLE_DEFAULT)
                .orElseGet(() -> {
                    log.warn("Rol predeterminado no encontrado, creándolo ahora");
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombreRol(Rol.ROLE_DEFAULT);
                    nuevoRol.setDescripcion("Rol predeterminado para nuevos usuarios");
                    return rolRepository.save(nuevoRol);
                });
    }

    /**
     * Construye un objeto Usuario a partir de los datos de registro.
     */
    private Usuario buildUsuarioFromRequest(RegisterRequest request, Persona persona, Rol rol) {
        return Usuario.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .persona(persona)
                .rol(rol)
                .estado(EstadoUsuario.ACTIVO)
                .imagenPerfil(DEFAULT_PROFILE_IMAGE)
                .intentosFallidos(0)
                .build();
    }

    /**
     * Construye un objeto Contrato a partir de los datos de registro.
     */
    private Contrato buildContratoFromRequest(RegisterRequest request, Rol rol) {
        ContratoDTO contratoDTO = ContratoDTO.builder()
                .fechaInicio(request.getFechaInicioContrato())
                .fechaFin(request.getFechaFinContrato())
                .build();

        Contrato contrato = contratoMapper.toEntity(contratoDTO);

        contrato.setCargo(rol.getNombreRol());
        contrato.setDescripcion("Contrato generado durante el registro");

        return contrato;
    }

    /**
     * Determina si un rol requiere un contrato.
     */
    private boolean requiresContract(String rol) {
        return !rol.equals(Rol.ROLE_PACIENTE) &&
                !rol.equals(Rol.ROLE_DEFAULT) &&
                !rol.equals(Rol.ROLE_ADMIN);
    }

    /**
     * Registra la actividad de registro en auditoría o sistemas de seguimiento.
     */
    private void auditUserRegistration(Usuario usuario) {
        // Implementación de auditoría...
        log.debug("Auditoría: Nuevo usuario registrado - {} con rol {}",
                usuario.getNombreUsuario(), usuario.getRol().getNombreRol());
    }

    /**
     * Verifica si la cuenta está bloqueada por intentos fallidos.
     */
    private void checkAccountLockStatus(Usuario usuario) {
        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            log.warn("Intento de acceso a cuenta suspendida: {}", usuario.getNombreUsuario());
            throw new InvalidOperationException("Esta cuenta ha sido suspendida. Contacte al administrador.");
        }

        if (usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() >= MAX_LOGIN_ATTEMPTS) {
            log.warn("Cuenta bloqueada por intentos fallidos excesivos: {}", usuario.getNombreUsuario());
            usuario.setEstado(EstadoUsuario.SUSPENDIDO);
            usuarioRepository.save(usuario);
            throw new InvalidOperationException("Cuenta bloqueada por intentos fallidos excesivos. Contacte al administrador.");
        }
    }

    /**
     * Maneja un intento fallido de login, incrementando el contador.
     */
    private void handleFailedLoginAttempt(Usuario usuario) {
        usuario.registrarIntentoFallido();
        usuarioRepository.save(usuario);
        log.warn("Intento fallido de login para usuario: {}. Intentos fallidos: {}",
                usuario.getNombreUsuario(), usuario.getIntentosFallidos());

        if (usuario.getIntentosFallidos() >= MAX_LOGIN_ATTEMPTS) {
            usuario.setEstado(EstadoUsuario.SUSPENDIDO);
            usuarioRepository.save(usuario);
            log.warn("Usuario {} suspendido por exceder intentos fallidos", usuario.getNombreUsuario());
        }
    }

    /**
     * Valida el usuario para el refresco de token.
     */
    private void validateUserForTokenRefresh(Usuario usuario) {
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            log.warn("Intento de refresh token con usuario no activo: {}", usuario.getNombreUsuario());
            throw new InvalidOperationException("La cuenta de usuario no está activa");
        }
    }

    /**
     * Autentica a un usuario utilizando el AuthenticationManager.
     */
    private Authentication authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(authRequest);
        log.debug("Usuario autenticado con éxito: {}", username);
        return authentication;
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     */
    private Usuario getUserByUsername(String username) {
        return usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado: {}", username);
                    return new ResourceNotFoundException("Usuario no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    /**
     * Obtiene un cliente OAuth2 registrado por su ID.
     */
    private RegisteredClient getRegisteredClient(String clientId) {
        RegisteredClient client = clientRepository.findById(clientId);
        if (client == null) {
            log.error("Cliente OAuth2 '{}' no encontrado", clientId);
            throw new IllegalStateException("Cliente OAuth2 no configurado correctamente");
        }
        return client;
    }

    /**
     * Genera un token de acceso OAuth2 con claims personalizados.
     */
    private OAuth2AccessToken generateAccessToken(Authentication authentication, RegisteredClient client, Usuario usuario) {
        Set<String> authorizedScopes = new HashSet<>(client.getScopes());

        // Crear contexto del token con información adicional
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("username", usuario.getNombreUsuario());
        additionalParameters.put("rol", usuario.getRol().getNombreRol());
        additionalParameters.put("nombreCompleto", usuario.getPersona().getNombreCompleto());
        additionalParameters.put("dni", usuario.getPersona().getDni());

        // Añadir información específica según el rol
        if (usuario.getRol().getNombreRol().equals(Rol.ROLE_MEDICO) ||
                usuario.getRol().getNombreRol().equals(Rol.ROLE_ENFERMERO)) {

            Contrato contratoActivo = usuario.getContratoActivo();
            if (contratoActivo != null) {
                additionalParameters.put("cargo", contratoActivo.getCargo());
            }
        }

        // Construir el contexto del token correctamente
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(client)
                .principal(authentication)
                .authorizationGrantType(PASSWORD_GRANT)
                .authorizedScopes(authorizedScopes)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN);

        // Añadir cada parámetro adicional como atributo del contexto
        additionalParameters.forEach(tokenContextBuilder::put);

        OAuth2TokenContext tokenContext = tokenContextBuilder.build();

        OAuth2Token token = tokenGenerator.generate(tokenContext);
        if (token == null) {
            log.error("No se pudo generar el token de acceso");
            throw new IllegalStateException("Error al generar el token de acceso");
        }

        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                token.getTokenValue(),
                token.getIssuedAt(),
                token.getExpiresAt(),
                authorizedScopes
        );
    }

    /**
     * Genera un refresh token OAuth2.
     */
    private OAuth2RefreshToken generateRefreshToken(Authentication authentication, RegisteredClient client) {
        OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(client)
                .principal(authentication)
                .authorizationGrantType(PASSWORD_GRANT)
                .tokenType(OAuth2TokenType.REFRESH_TOKEN)
                .build();

        OAuth2Token token = tokenGenerator.generate(tokenContext);
        if (token == null) {
            log.warn("No se pudo generar el refresh token");
            return null;
        }

        return new OAuth2RefreshToken(
                token.getTokenValue(),
                token.getIssuedAt(),
                token.getExpiresAt()
        );
    }

    /**
     * Guarda la autorización del token en el servicio OAuth2.
     */
    private void saveTokenAuthorization(String principalName, OAuth2AccessToken accessToken,
                                        OAuth2RefreshToken refreshToken, String registeredClientId, Usuario usuario) {
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(
                Objects.requireNonNull(clientRepository.findById(registeredClientId)));

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("rol", usuario.getRol().getNombreRol());
        metadata.put("dni", usuario.getPersona().getDni());
        metadata.put("lastLogin", LocalDateTime.now().toString());

        authorizationBuilder
                .principalName(principalName)
                .authorizationGrantType(PASSWORD_GRANT)
                .token(accessToken, (metadataMap) -> metadataMap.putAll(metadata))
                .attribute("username", principalName);

        if (refreshToken != null) {
            authorizationBuilder.refreshToken(refreshToken);
        }

        OAuth2Authorization authorization = authorizationBuilder.build();
        authorizationService.save(authorization);
    }

    /**
     * Encuentra una autorización por su token de refresh.
     */
    private OAuth2Authorization findAuthorizationByToken(String tokenValue) {
        OAuth2Authorization authorization = authorizationService.findByToken(
                tokenValue, OAuth2TokenType.REFRESH_TOKEN);

        if (authorization == null) {
            log.warn("Token de actualización no encontrado en el sistema");
            throw new InvalidTokenException("Token de actualización no válido");
        }

        return authorization;
    }

    /**
     * Valida que el refresh token sea válido y no haya expirado.
     */
    private void validateRefreshToken(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();

        if (refreshToken == null) {
            log.warn("No se encontró el refresh token en la autorización");
            throw new InvalidTokenException("Token de actualización inválido");
        }

        if (refreshToken.getToken().getExpiresAt() != null &&
                refreshToken.getToken().getExpiresAt().isBefore(Instant.now())) {
            log.warn("Token de actualización expirado");
            throw new InvalidTokenException("El token de actualización ha expirado");
        }
    }

    /**
     * Crea un objeto Authentication basado en un usuario.
     */
    private Authentication createAuthenticationFromUser(Usuario usuario) {
        Collection<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol()));

        return new UsernamePasswordAuthenticationToken(
                usuario.getNombreUsuario(), null, authorities);
    }

    /**
     * Extrae el nombre de usuario de un token JWT.
     */
    private String extractUsernameFromToken(String token) {
        try {
            // Eliminar el prefijo "Bearer " si existe
            String tokenValue = token;
            if (token.startsWith(TOKEN_PREFIX)) {
                tokenValue = token.substring(TOKEN_PREFIX.length());
            }

            Jwt jwt = jwtDecoder.decode(tokenValue);

            // Intentar obtener el username de diferentes claims estándar
            String username = jwt.getClaimAsString("username");
            if (username == null) {
                username = jwt.getClaimAsString("sub");
            }
            if (username == null) {
                username = jwt.getClaimAsString(JwtClaimNames.SUB);
            }

            if (username == null) {
                throw new InvalidTokenException("No se pudo extraer el nombre de usuario del token");
            }

            return username;
        } catch (JwtException e) {
            log.error("Error al decodificar token JWT", e);
            throw new InvalidTokenException("Token inválido: " + e.getMessage());
        }
    }

    /**
     * Verifica si el token proporcionado es válido.
     *
     * @param token Token JWT a validar
     * @return true si el token es válido
     */
    public boolean validateToken(String token) {
        try {
            // Eliminar el prefijo "Bearer " si existe
            String tokenValue = token;
            if (token.startsWith(TOKEN_PREFIX)) {
                tokenValue = token.substring(TOKEN_PREFIX.length());
            }

            Jwt jwt = jwtDecoder.decode(tokenValue);

            // Verificar fecha de expiración
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(Instant.now())) {
                log.warn("Token expirado");
                return false;
            }

            // Verificar que el usuario exista y esté activo
            String username = extractUsernameFromToken(token);
            Usuario usuario = getUserByUsername(username);

            return usuario.getEstado() == EstadoUsuario.ACTIVO;
        } catch (Exception e) {
            log.warn("Error al validar token", e);
            return false;
        }
    }
}