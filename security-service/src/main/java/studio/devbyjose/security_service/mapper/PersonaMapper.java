package studio.devbyjose.security_service.mapper;

import org.mapstruct.*;
import studio.devbyjose.security_service.dto.PersonaDTO;
import studio.devbyjose.security_service.dto.RegisterRequest;
import studio.devbyjose.security_service.entity.Persona;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
        imports = {LocalDate.class, Period.class})
public interface PersonaMapper {

    @Mapping(target = "dni", source = "dni")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "apellido", source = "apellido")
    @Mapping(target = "telefono", source = "telefono")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fechaNacimiento", source = "fechaNacimiento")
    @Mapping(target = "sexo", source = "sexo", defaultValue = "N")
    @Mapping(target = "direccion", source = "direccion")
    Persona toEntity(RegisterRequest request);

    @Mapping(target = "dni", source = "dni")
    @Mapping(target = "edad", expression = "java(calcularEdad(persona.getFechaNacimiento()))")
    PersonaDTO toDto(Persona persona);

    List<PersonaDTO> toDtoList(List<Persona> personas);

    @Named("calcularEdad")
    default Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonaFromDto(PersonaDTO dto, @MappingTarget Persona persona);
}