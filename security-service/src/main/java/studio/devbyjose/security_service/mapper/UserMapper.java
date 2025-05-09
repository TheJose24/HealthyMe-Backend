package studio.devbyjose.security_service.mapper;

import org.mapstruct.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import studio.devbyjose.security_service.dto.*;
import studio.devbyjose.security_service.entity.Contrato;
import studio.devbyjose.security_service.entity.Usuario;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {PersonaMapper.class, RolMapper.class, ContratoMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
        imports = {LocalDate.class})
public interface UserMapper {

    @Named("toDto")
    @Mapping(target = "rol", source = "rol.nombreRol")
    UserDTO toDto(Usuario usuario);

    @Named("toDtoWithPersona")
    @Mapping(target = "rol", source = "rol.nombreRol")
    UserDTO toDtoWithPersona(Usuario usuario);

    @Named("toDtoComplete")
    @Mapping(target = "contratos", source = "contratos")
    @Mapping(target = "rol", source = "rol.nombreRol")
    UserDTO toDtoComplete(Usuario usuario);

    @Mapping(target = "accessToken", source = "accessToken.tokenValue")
    @Mapping(target = "refreshToken", source = "refreshToken.tokenValue")
    @Mapping(target = "username", source = "usuario.nombreUsuario")
    @Mapping(target = "rol", source = "usuario.rol.nombreRol")
    AuthResponse toAuthResponse(Usuario usuario, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken);

    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUsuarioFromDto(UserDTO dto, @MappingTarget Usuario usuario);

    @IterableMapping(qualifiedByName = "toDto")
    List<UserDTO> toDtoList(List<Usuario> usuarios);

    @IterableMapping(qualifiedByName = "toDtoComplete")
    List<UserDTO> toDtoCompleteList(List<Usuario> usuarios);
}