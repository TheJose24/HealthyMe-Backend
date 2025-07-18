package studio.devbyjose.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 🌍 Configurar CORS usando tu configuración personalizada
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // 🚫 Deshabilitar CSRF para APIs REST
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                
                // 🔓 Rutas públicas (sin autenticación)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                            "/actuator/**",
                            "/fallback/**",
                            "/auth/**",
                            "/api/auth/**",
                            "/api/public/**"
                        ).permitAll()
                        
                        // 🔒 Todas las demás rutas requieren autenticación
                        .anyExchange().authenticated()
                )
                
                // ⚙️ Configurar OAuth2 Resource Server para validar JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // URL del servidor de autorización (tu security-service)
                                .jwkSetUri("http://security-service:9000/.well-known/jwks.json")
                        )
                )
                
                .build();
    }
}