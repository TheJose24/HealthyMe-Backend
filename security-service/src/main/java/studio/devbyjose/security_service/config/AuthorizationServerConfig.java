package studio.devbyjose.security_service.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import studio.devbyjose.security_service.entity.Usuario;
import studio.devbyjose.security_service.repository.UsuarioRepository;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfig {

    private final PasswordEncoder passwordEncoder;

    public AuthorizationServerConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
        // Primero, registramos los clientes usando InMemoryRegisteredClientRepository
        RegisteredClient webClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("healthyme-web")
                .clientSecret(passwordEncoder.encode("web-secret"))
                .clientName("HealthyMe Web App")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:4200/login/oauth2/code/healthyme")
                .redirectUri("http://localhost:4200/login/oauth2/code/healthyme")
                .scope(OidcScopes.OPENID)
                .scope("medicos.read")
                .scope("medicos.write")
                .scope("pacientes.read")
                .scope("pacientes.write")
                .scope("citas.read")
                .scope("citas.write")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(30))
                        .reuseRefreshTokens(false)
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .build())
                .build();

        RegisteredClient mobileClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("healthyme-mobile")
                .clientSecret(passwordEncoder.encode("mobile-secret"))
                .clientName("HealthyMe Mobile App")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("com.healthyme.app:/oauth2/callback")
                .scope(OidcScopes.OPENID)
                .scope("medicos.read")
                .scope("pacientes.read")
                .scope("citas.read")
                .scope("citas.write")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(30))
                        .reuseRefreshTokens(false)
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .build())
                .build();

        RegisteredClient serviceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("healthyme-service")
                .clientSecret(passwordEncoder.encode("service-secret"))
                .clientName("HealthyMe Service Client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("services.all")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .build())
                .build();

        // Usar JdbcRegisteredClientRepository para persistir los clientes
        JdbcRegisteredClientRepository jdbcRegisteredClientRepository =
                new JdbcRegisteredClientRepository(jdbcOperations);

        // Guardar los clientes en la base de datos
        Arrays.asList(webClient, mobileClient, serviceClient).forEach(client -> {
            // Verificar si el cliente ya existe antes de guardarlo
            try {
                if (jdbcRegisteredClientRepository.findByClientId(client.getClientId()) == null) {
                    jdbcRegisteredClientRepository.save(client);
                }
            } catch (Exception e) {
                // Si hay un error al buscar, intentamos guardar
                jdbcRegisteredClientRepository.save(client);
            }
        });

        return jdbcRegisteredClientRepository;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Error generando claves RSA", e);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://security-service:8085/auth")
                .build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UsuarioRepository usuarioRepository) {
        return context -> {
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                // Datos básicos del token
                context.getClaims().claim("iss", "http://security-service:8085/auth");

                // Obtener datos del usuario si está disponible
                Authentication principal = context.getPrincipal();
                if (principal != null) {
                    String username = principal.getName();
                    Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(username);

                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        // Añadir claims comunes
                        context.getClaims()
                                .claim("username", usuario.getNombreUsuario())
                                .claim("rol", usuario.getRol().getNombreRol())
                                .claim("userId", usuario.getIdUsuario());

                        // Datos personales si existen
                        if (usuario.getPersona() != null) {
                            context.getClaims()
                                    .claim("nombreCompleto", usuario.getPersona().getNombreCompleto())
                                    .claim("dni", usuario.getPersona().getDni());
                        }

                        // Datos de contrato si aplica
                        if (usuario.getContratoActivo() != null) {
                            context.getClaims()
                                    .claim("cargo", usuario.getContratoActivo().getCargo());
                        }
                    }
                }
            }
        };
    }

}