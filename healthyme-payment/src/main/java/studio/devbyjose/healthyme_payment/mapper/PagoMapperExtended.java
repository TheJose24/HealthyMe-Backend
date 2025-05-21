package studio.devbyjose.healthyme_payment.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import studio.devbyjose.healthyme_payment.dto.PagoResponseDTO;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.entity.Transaccion;
import studio.devbyjose.healthyme_payment.repository.FacturaRepository;
import studio.devbyjose.healthyme_payment.repository.TransaccionRepository;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MetodoPagoMapper.class, FacturaMapper.class, TransaccionMapper.class})
public abstract class PagoMapperExtended {

    @Autowired
    protected FacturaRepository facturaRepository;

    @Autowired
    protected TransaccionRepository transaccionRepository;

    @Mapping(target = "factura", source = "pago", qualifiedByName = "pagoToFactura")
    @Mapping(target = "transacciones", source = "pago", qualifiedByName = "pagoToTransacciones")
    public abstract PagoResponseDTO toResponseDto(Pago pago);

    @Named("pagoToFactura")
    protected Factura pagoToFactura(Pago pago) {
        if (pago == null) return null;
        return facturaRepository.findByPago(pago).orElse(null);
    }

    @Named("pagoToTransacciones")
    protected List<Transaccion> pagoToTransacciones(Pago pago) {
        if (pago == null) return null;
        return transaccionRepository.findByPago(pago);
    }
}