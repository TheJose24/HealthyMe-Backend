package dev.juliancamacho.healthyme_personal.mapper;

import dev.juliancamacho.healthyme_personal.dto.AdministradorDto;
import dev.juliancamacho.healthyme_personal.entity.Administrador;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdministradorMapper {
    // Entity -> DTO
    AdministradorDto administradorToAdministradorDto(Administrador administrador);

    // DTO -> Entity
    Administrador administradorDtoToAdministrador(AdministradorDto administradorDto);

    // List<Entity> -> List<DTO>
    List<AdministradorDto> administradoresToAdministradorDtos(List<Administrador> administradores);
}
