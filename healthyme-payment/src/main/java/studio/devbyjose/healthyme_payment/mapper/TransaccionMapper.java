package studio.devbyjose.healthyme_payment.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import studio.devbyjose.healthyme_payment.dto.CreateTransaccionDTO;
import studio.devbyjose.healthyme_payment.dto.TransaccionDTO;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.entity.Transaccion;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {LocalDateTime.class})
public abstract class TransaccionMapper {

    @Autowired
    protected PagoRepository pagoRepository;

    @Mapping(target = "fechaTransaccion", expression = "java(LocalDateTime.now())")
    @Mapping(target = "pago", source = "idPago", qualifiedByName = "idToPago")
    @Mapping(target = "id", ignore = true)
    public abstract Transaccion toEntity(CreateTransaccionDTO dto);

    @Mapping(target = "idPago", source = "pago.id")
    public abstract TransaccionDTO toDto(Transaccion entity);

    public abstract List<TransaccionDTO> toDtoList(List<Transaccion> entities);

    @Named("idToPago")
    protected Pago idToPago(Integer id) {
        if (id == null) return null;
        return pagoRepository.findById(id).orElse(null);
    }
}