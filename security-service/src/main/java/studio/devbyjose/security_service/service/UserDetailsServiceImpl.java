package studio.devbyjose.security_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import studio.devbyjose.security_service.entity.Usuario;
import studio.devbyjose.security_service.enums.EstadoUsuario;
import studio.devbyjose.security_service.repository.UsuarioRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Verificar si el usuario está activo
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new UsernameNotFoundException("Usuario no está activo: " + username);
        }

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getContrasena())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol())))
                .accountExpired(false)
                .accountLocked(usuario.getEstado() == EstadoUsuario.SUSPENDIDO)
                .credentialsExpired(false)
                .disabled(usuario.getEstado() == EstadoUsuario.ELIMINADO)
                .build();
    }
}