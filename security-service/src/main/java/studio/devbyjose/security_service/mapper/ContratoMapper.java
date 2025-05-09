package studio.devbyjose.security_service.mapper;

import org.mapstruct.*;
import studio.devbyjose.security_service.dto.ContratoDTO;
import studio.devbyjose.security_service.dto.ContratoDetalleDTO;
import studio.devbyjose.security_service.entity.Contrato;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
        imports = {LocalDate.class})
public interface ContratoMapper {

    @Mapping(target = "idContrato", source = "idContrato")
    @Mapping(target = "fechaInicio", source = "fechaInicio")
    @Mapping(target = "fechaFin", source = "fechaFin")
    @Mapping(target = "activo", expression = "java(isContratoActivo(contrato))")
    ContratoDTO toDto(Contrato contrato);

    @Mapping(target = "usuario", ignore = true)
    Contrato toEntity(ContratoDTO dto);

    List<ContratoDTO> toDtoList(List<Contrato> contratos);

    @Named("isContratoActivo")
    default boolean isContratoActivo(Contrato contrato) {
        if (contrato == null || contrato.getFechaFin() == null) {
            return false;
        }
        return LocalDate.now().isBefore(contrato.getFechaFin()) &&
                (LocalDate.now().isAfter(contrato.getFechaInicio()) ||
                        LocalDate.now().isEqual(contrato.getFechaInicio()));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateContratoFromDto(ContratoDTO dto, @MappingTarget Contrato contrato);

    @Mapping(target = "usuario", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateContratoFromDetalleDto(ContratoDetalleDTO dto, @MappingTarget Contrato contrato);

    @Named("toDetalleDto")
    @Mapping(target = "idContrato", source = "idContrato")
    @Mapping(target = "fechaInicio", source = "fechaInicio")
    @Mapping(target = "fechaFin", source = "fechaFin")
    @Mapping(target = "activo", expression = "java(isContratoActivo(contrato))")
    @Mapping(target = "cargo", source = "cargo")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "salario", source = "salario")
    @Mapping(target = "horasSemana", source = "horasSemana")
    ContratoDetalleDTO toDetalleDto(Contrato contrato);

    @IterableMapping(qualifiedByName = "toDetalleDto")
    List<ContratoDetalleDTO> toDetalleDtoList(List<Contrato> contratos);
}