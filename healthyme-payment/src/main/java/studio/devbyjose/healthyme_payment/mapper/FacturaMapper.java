package studio.devbyjose.healthyme_payment.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import studio.devbyjose.healthyme_commons.client.dto.FacturaDTO;
import studio.devbyjose.healthyme_payment.dto.CreateFacturaDTO;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {LocalDateTime.class, DateTimeFormatter.class})
public abstract class FacturaMapper {

    @Autowired
    protected PagoRepository pagoRepository;

    @Mapping(target = "fechaEmision", expression = "java(LocalDateTime.now())")
    @Mapping(target = "numeroFactura", expression = "java(generateNumeroFactura())")
    @Mapping(target = "pago", source = "idPago", qualifiedByName = "idToPago")
    @Mapping(target = "id", ignore = true)
    public abstract Factura toEntity(CreateFacturaDTO dto);

    @Mapping(target = "idPago", source = "pago.id")
    public abstract FacturaDTO toDto(Factura entity);

    public abstract List<FacturaDTO> toDtoList(List<Factura> entities);

    @Named("idToPago")
    protected Pago idToPago(Integer id) {
        if (id == null) return null;
        return pagoRepository.findById(id).orElse(null);
    }

    protected String generateNumeroFactura() {
        // Formato: F-YYYYMMDD-HHMMSS
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return "F-" + LocalDateTime.now().format(formatter);
    }
}