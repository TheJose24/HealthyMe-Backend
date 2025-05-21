package studio.devbyjose.healthyme_payment.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import studio.devbyjose.healthyme_commons.client.dto.CreatePagoDTO;
import studio.devbyjose.healthyme_commons.client.dto.PagoDTO;
import studio.devbyjose.healthyme_payment.entity.MetodoPago;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.repository.MetodoPagoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MetodoPagoMapper.class},
        imports = {LocalDateTime.class})
public abstract class PagoMapper {

    @Autowired
    protected MetodoPagoRepository metodoPagoRepository;

    @Mapping(target = "fechaPago", expression = "java(LocalDateTime.now())")
    @Mapping(target = "metodoPago", source = "idMetodoPago", qualifiedByName = "idToMetodoPago")
    @Mapping(target = "id", ignore = true)
    public abstract Pago toEntity(CreatePagoDTO dto);

    @Mapping(target = "metodoPago", source = "metodoPago")
    public abstract PagoDTO toDto(Pago entity);

    public abstract List<PagoDTO> toDtoList(List<Pago> entities);

    @Named("idToMetodoPago")
    protected MetodoPago idToMetodoPago(Integer id) {
        if (id == null) return null;
        return metodoPagoRepository.findById(id).orElse(null);
    }
}