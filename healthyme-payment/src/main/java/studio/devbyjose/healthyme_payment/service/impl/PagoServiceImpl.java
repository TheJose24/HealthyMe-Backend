package studio.devbyjose.healthyme_payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_commons.client.dto.CreatePagoDTO;
import studio.devbyjose.healthyme_commons.client.dto.PagoDTO;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;
import studio.devbyjose.healthyme_payment.dto.CreateFacturaDTO;
import studio.devbyjose.healthyme_payment.dto.PagoResponseDTO;
import studio.devbyjose.healthyme_payment.dto.PaymentIntentDTO;
import studio.devbyjose.healthyme_payment.entity.MetodoPago;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.entity.Transaccion;
import studio.devbyjose.healthyme_payment.exception.InvalidPaymentStateException;
import studio.devbyjose.healthyme_payment.exception.MetodoPagoNotFoundException;
import studio.devbyjose.healthyme_payment.exception.PaymentNotFoundException;
import studio.devbyjose.healthyme_payment.exception.PaymentProcessingException;
import studio.devbyjose.healthyme_payment.mapper.PagoMapper;
import studio.devbyjose.healthyme_payment.mapper.PagoMapperExtended;
import studio.devbyjose.healthyme_payment.repository.MetodoPagoRepository;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;
import studio.devbyjose.healthyme_payment.repository.TransaccionRepository;
import studio.devbyjose.healthyme_payment.service.interfaces.FacturaService;
import studio.devbyjose.healthyme_payment.service.interfaces.NotificationService;
import studio.devbyjose.healthyme_payment.service.interfaces.PagoService;
import studio.devbyjose.healthyme_payment.service.interfaces.StripeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final TransaccionRepository transaccionRepository;
    private final PagoMapper pagoMapper;
    private final PagoMapperExtended pagoMapperExtended;
    private final FacturaService facturaService;
    private final StripeService stripeService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PagoDTO createPago(CreatePagoDTO createPagoDTO) {
        // Verificar si el método de pago existe
        MetodoPago metodoPago = metodoPagoRepository.findById(createPagoDTO.getIdMetodoPago())
                .orElseThrow(() -> new MetodoPagoNotFoundException(createPagoDTO.getIdMetodoPago()));

        // Crear el pago con estado PENDIENTE
        Pago pago = Pago.builder()
                .monto(createPagoDTO.getMonto())
                .fechaPago(LocalDateTime.now())
                .estado(EstadoPago.PENDIENTE)
                .metodoPago(metodoPago)
                .entidadReferencia(createPagoDTO.getEntidadReferencia())
                .entidadReferenciaId(createPagoDTO.getIdReferencia())
                .idPaciente(createPagoDTO.getIdPaciente())
                .build();

        Pago savedPago = pagoRepository.save(pago);
        return pagoMapper.toDto(savedPago);
    }

    @Override
    @Transactional
    public PagoResponseDTO processPago(Integer idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PaymentNotFoundException(idPago));

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new InvalidPaymentStateException("El pago no está en estado PENDIENTE para ser procesado");
        }

        try {
            // Crear intención de pago en Stripe
            Map<String, String> metadata = new HashMap<>();
            metadata.put("id_pago", idPago.toString());
            metadata.put("entidad_referencia", pago.getEntidadReferencia().name());
            metadata.put("id_referencia", pago.getEntidadReferenciaId().toString());

            PaymentIntentDTO paymentIntentData = stripeService.createPaymentIntent(
                    pago.getMonto(),
                    "Pago " + pago.getEntidadReferencia() + " #" + pago.getEntidadReferenciaId(),
                    metadata
            );

            // Registrar transacción
            Transaccion transaccion = new Transaccion();
            transaccion.setPago(pago);
            transaccion.setFechaTransaccion(LocalDateTime.now());
            transaccion.setReferenciaExterna(paymentIntentData.getId());

            Map<String, Object> datosTransaccion = new HashMap<>();
            datosTransaccion.put("payment_intent_id", paymentIntentData.getId());
            datosTransaccion.put("client_secret", paymentIntentData.getClientSecret());
            transaccion.setDatosTransaccion(datosTransaccion);

            transaccionRepository.save(transaccion);

            pago.setPaymentIntentId(paymentIntentData.getId());
            pagoRepository.save(pago);

            // Actualizar estado del pago (se mantiene en PENDIENTE hasta confirmación)
            return pagoMapperExtended.toResponseDto(pago);

        } catch (Exception e) {
            // En caso de error, marcar como fallido
            pago.setEstado(EstadoPago.FALLIDO);
            pagoRepository.save(pago);
            throw new PaymentProcessingException("Error al procesar el pago: " + e.getMessage(), e);
        }
    }

    @Override
    public PagoResponseDTO getPagoById(Integer idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PaymentNotFoundException(idPago));

        return pagoMapperExtended.toResponseDto(pago);
    }

    @Override
    public List<PagoDTO> getPagosByPaciente(Integer idPaciente) {
        List<Pago> pagos = pagoRepository.findByIdPaciente(idPaciente);
        return pagoMapper.toDtoList(pagos);
    }

    @Override
    public List<PagoDTO> getPagosByEstado(EstadoPago estado) {
        List<Pago> pagos = pagoRepository.findByEstado(estado);
        return pagoMapper.toDtoList(pagos);
    }

    @Override
    @Transactional
    public PagoDTO updateEstadoPago(Integer idPago, EstadoPago estado) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PaymentNotFoundException(idPago));

        pago.setEstado(estado);
        Pago updatedPago = pagoRepository.save(pago);

        // Si el pago se completó, generar factura
        if (estado == EstadoPago.COMPLETADO) {
            try {
                // Calcular impuestos (18% del monto)
                BigDecimal subtotal = pago.getMonto().divide(new BigDecimal("1.18"), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal impuestos = pago.getMonto().subtract(subtotal);

                CreateFacturaDTO createFacturaDTO = new CreateFacturaDTO();
                createFacturaDTO.setSubtotal(subtotal);
                createFacturaDTO.setImpuestos(impuestos);
                createFacturaDTO.setTotal(pago.getMonto());
                createFacturaDTO.setIdPago(pago.getId());

                facturaService.createFactura(createFacturaDTO);

                // Enviar notificación
                if (pago.getPaymentIntentId() != null) {
                    notificationService.notificarPagoExitoso(pago.getPaymentIntentId());
                } else {
                    log.warn("No se puede enviar notificación: paymentIntentId es nulo para el pago ID: {}", pago.getId());
                }
            } catch (Exception e) {
                throw new PaymentProcessingException("Error al completar el proceso de pago: " + e.getMessage(), e);
            }
        }

        return pagoMapper.toDto(updatedPago);
    }

    @Override
    public PagoDTO getPagoByReferencia(EntidadOrigen entidadReferencia, Integer idReferencia) {
        Pago pago = pagoRepository.findByEntidadReferenciaAndEntidadReferenciaId(entidadReferencia, idReferencia)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Pago no encontrado para " + entidadReferencia + " con ID: " + idReferencia));

        return pagoMapper.toDto(pago);
    }
}