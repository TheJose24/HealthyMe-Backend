package studio.devbyjose.security_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import studio.devbyjose.security_service.dto.RolDTO;
import studio.devbyjose.security_service.entity.Rol;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface RolMapper {

    @Mapping(target = "idRol", source = "idRol")
    @Mapping(target = "nombreRol", source = "nombreRol")
    RolDTO toDto(Rol rol);

    @Mapping(target = "idRol", source = "idRol")
    @Mapping(target = "nombreRol", source = "nombreRol")
    Rol toEntity(RolDTO dto);

    List<RolDTO> toDtoList(List<Rol> roles);
}