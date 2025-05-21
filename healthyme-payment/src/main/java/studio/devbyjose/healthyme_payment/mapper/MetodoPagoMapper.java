package studio.devbyjose.healthyme_payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import studio.devbyjose.healthyme_commons.client.dto.MetodoPagoDTO;
import studio.devbyjose.healthyme_payment.dto.CreateMetodoPagoDTO;
import studio.devbyjose.healthyme_payment.entity.MetodoPago;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MetodoPagoMapper {

    @Mapping(target = "id", ignore = true)
    public abstract MetodoPago toEntity(MetodoPagoDTO dto);

    @Mapping(target = "id", ignore = true)
    public abstract MetodoPago toEntity(CreateMetodoPagoDTO dto);

    public abstract MetodoPagoDTO toDto(MetodoPago entity);

    public abstract List<MetodoPagoDTO> toDtoList(List<MetodoPago> entities);
}