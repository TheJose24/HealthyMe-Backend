package studio.devbyjose.healthyme_payment.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;
import studio.devbyjose.healthyme_commons.enums.payment.TipoMetodoPago;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.MetodoPago;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.entity.Transaccion;
import studio.devbyjose.healthyme_payment.repository.FacturaRepository;
import studio.devbyjose.healthyme_payment.repository.MetodoPagoRepository;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;
import studio.devbyjose.healthyme_payment.repository.TransaccionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo-data seeder for healthyme-payment (DB healthyme_payment).
 * Insert order: MetodoPago -> Pago -> (Factura, Transaccion).
 * IDs are IDENTITY; tables must be empty so auto-increment yields the canonical ids.
 * Enum note: the canonical plan's TipoMetodoPago.TARJETA_CREDITO / BILLETERA_DIGITAL do not
 * exist. Real constants are {TARJETA, EFECTIVO, TRANSFERENCIA, SEGURO}; card + digital-wallet
 * methods both map to TARJETA. EstadoPago uses COMPLETADO (paid) and PENDIENTE.
 */
@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final MetodoPagoRepository metodoPagoRepository;
    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final TransaccionRepository transaccionRepository;

    @Override
    public void run(String... args) {
        List<MetodoPago> metodos = seedMetodosPago();
        List<Pago> pagos = seedPagos(metodos);
        seedFacturas(pagos);
        seedTransacciones(pagos);
    }

    private List<MetodoPago> seedMetodosPago() {
        if (metodoPagoRepository.count() > 0) {
            log.info("[seed] MetodoPago already present, skipping");
            return metodoPagoRepository.findAll();
        }
        List<MetodoPago> metodos = List.of(
                MetodoPago.builder().tipo(TipoMetodoPago.EFECTIVO).nombre("Efectivo").estado(true).build(),
                MetodoPago.builder().tipo(TipoMetodoPago.TARJETA).nombre("Tarjeta de crédito Visa/Mastercard").estado(true).build(),
                MetodoPago.builder().tipo(TipoMetodoPago.TRANSFERENCIA).nombre("Transferencia bancaria BCP").estado(true).build(),
                MetodoPago.builder().tipo(TipoMetodoPago.TARJETA).nombre("Yape / Plin").estado(true).build()
        );
        List<MetodoPago> saved = metodoPagoRepository.saveAll(metodos);
        log.info("[seed] MetodoPago inserted: {}", saved.size());
        return saved;
    }

    private List<Pago> seedPagos(List<MetodoPago> metodos) {
        if (pagoRepository.count() > 0) {
            log.info("[seed] Pago already present, skipping");
            return pagoRepository.findAll();
        }
        List<Pago> pagos = new ArrayList<>();
        // monto, fechaPago, estado, metodoIdx(1-4), entidadReferencia, entidadReferenciaId, idPaciente, paymentIntentId
        pagos.add(pago("150.00", "2026-06-01T09:30", EstadoPago.COMPLETADO, metodos.get(1), EntidadOrigen.CONSULTA, 1, 1L, "pi_3Nx0001abcd"));
        pagos.add(pago("120.00", "2026-06-02T10:00", EstadoPago.COMPLETADO, metodos.get(0), EntidadOrigen.CONSULTA, 2, 2L, null));
        pagos.add(pago("80.00", "2026-06-03T10:30", EstadoPago.COMPLETADO, metodos.get(3), EntidadOrigen.EXAMEN, 3, 3L, null));
        pagos.add(pago("150.00", "2026-06-04T09:15", EstadoPago.COMPLETADO, metodos.get(1), EntidadOrigen.CONSULTA, 4, 4L, "pi_3Nx0004abcd"));
        pagos.add(pago("100.00", "2026-06-05T11:30", EstadoPago.COMPLETADO, metodos.get(2), EntidadOrigen.CONSULTA, 5, 5L, null));
        pagos.add(pago("90.00", "2026-06-08T11:00", EstadoPago.COMPLETADO, metodos.get(1), EntidadOrigen.CONSULTA, 7, 7L, "pi_3Nx0007abcd"));
        pagos.add(pago("200.00", "2026-06-09T09:00", EstadoPago.COMPLETADO, metodos.get(1), EntidadOrigen.CONSULTA, 8, 8L, "pi_3Nx0008abcd"));
        pagos.add(pago("60.00", "2026-06-11T11:45", EstadoPago.COMPLETADO, metodos.get(0), EntidadOrigen.EXAMEN, 7, 10L, null));
        pagos.add(pago("150.00", "2026-06-12T08:30", EstadoPago.COMPLETADO, metodos.get(3), EntidadOrigen.CONSULTA, 11, 11L, null));
        pagos.add(pago("130.00", "2026-06-13T10:30", EstadoPago.COMPLETADO, metodos.get(1), EntidadOrigen.CONSULTA, 12, 12L, "pi_3Nx0012abcd"));
        pagos.add(pago("180.00", "2026-06-17T11:15", EstadoPago.COMPLETADO, metodos.get(2), EntidadOrigen.CONSULTA, 14, 15L, null));
        pagos.add(pago("110.00", "2026-06-19T12:00", EstadoPago.COMPLETADO, metodos.get(0), EntidadOrigen.CONSULTA, 15, 17L, null));
        pagos.add(pago("150.00", "2026-07-21T09:30", EstadoPago.PENDIENTE, metodos.get(1), EntidadOrigen.CONSULTA, 1, 1L, "pi_3Nx0016pend"));
        pagos.add(pago("95.00", "2026-07-22T10:30", EstadoPago.PENDIENTE, metodos.get(3), EntidadOrigen.EXAMEN, 11, 9L, null));
        pagos.add(pago("250.00", "2026-07-22T11:00", EstadoPago.PENDIENTE, metodos.get(1), EntidadOrigen.EXAMEN, 6, 8L, "pi_3Nx0018pend"));

        List<Pago> saved = pagoRepository.saveAll(pagos);
        log.info("[seed] Pago inserted: {}", saved.size());
        return saved;
    }

    private void seedFacturas(List<Pago> pagos) {
        if (facturaRepository.count() > 0) {
            log.info("[seed] Factura already present, skipping");
            return;
        }
        List<Factura> facturas = List.of(
                factura("F001-00000001", pagos.get(0), "2026-06-01T09:31", "127.12", "22.88", "150.00"),
                factura("F001-00000002", pagos.get(1), "2026-06-02T10:01", "101.69", "18.31", "120.00"),
                factura("F001-00000003", pagos.get(2), "2026-06-03T10:31", "67.80", "12.20", "80.00"),
                factura("F001-00000004", pagos.get(3), "2026-06-04T09:16", "127.12", "22.88", "150.00"),
                factura("F001-00000005", pagos.get(4), "2026-06-05T11:31", "84.75", "15.25", "100.00"),
                factura("F001-00000006", pagos.get(5), "2026-06-08T11:01", "76.27", "13.73", "90.00"),
                factura("F001-00000007", pagos.get(6), "2026-06-09T09:01", "169.49", "30.51", "200.00"),
                factura("F001-00000008", pagos.get(7), "2026-06-11T11:46", "50.85", "9.15", "60.00"),
                factura("F001-00000009", pagos.get(8), "2026-06-12T08:31", "127.12", "22.88", "150.00"),
                factura("F001-00000010", pagos.get(9), "2026-06-13T10:31", "110.17", "19.83", "130.00"),
                factura("F001-00000011", pagos.get(10), "2026-06-17T11:16", "152.54", "27.46", "180.00"),
                factura("F001-00000012", pagos.get(11), "2026-06-19T12:01", "93.22", "16.78", "110.00")
        );
        facturaRepository.saveAll(facturas);
        log.info("[seed] Factura inserted: {}", facturas.size());
    }

    private void seedTransacciones(List<Pago> pagos) {
        if (transaccionRepository.count() > 0) {
            log.info("[seed] Transaccion already present, skipping");
            return;
        }
        List<Transaccion> txs = List.of(
                tx(pagos.get(0), "ch_3Nx0001", "2026-06-01T09:30", datos("stripe", "succeeded", "visa")),
                tx(pagos.get(1), "EF-0602-01", "2026-06-02T10:00", datos("caja", "succeeded", null)),
                tx(pagos.get(2), "YAPE-0603-01", "2026-06-03T10:30", datos("yape", "succeeded", null)),
                tx(pagos.get(3), "ch_3Nx0004", "2026-06-04T09:15", datos("stripe", "succeeded", "mastercard")),
                tx(pagos.get(4), "TR-0605-01", "2026-06-05T11:30", datos("bcp", "succeeded", null)),
                tx(pagos.get(5), "ch_3Nx0007", "2026-06-08T11:00", datos("stripe", "succeeded", "visa")),
                tx(pagos.get(6), "ch_3Nx0008", "2026-06-09T09:00", datos("stripe", "succeeded", "visa")),
                tx(pagos.get(7), "EF-0611-01", "2026-06-11T11:45", datos("caja", "succeeded", null)),
                tx(pagos.get(8), "YAPE-0612-01", "2026-06-12T08:30", datos("yape", "succeeded", null)),
                tx(pagos.get(9), "ch_3Nx0012", "2026-06-13T10:30", datos("stripe", "succeeded", "mastercard")),
                tx(pagos.get(10), "TR-0617-01", "2026-06-17T11:15", datos("bcp", "succeeded", null)),
                tx(pagos.get(11), "EF-0619-01", "2026-06-19T12:00", datos("caja", "succeeded", null)),
                tx(pagos.get(12), "pi_3Nx0016", "2026-07-21T09:30", datos("stripe", "requires_payment_method", null)),
                tx(pagos.get(13), "YAPE-0722-01", "2026-07-22T10:30", datos("yape", "pending", null)),
                tx(pagos.get(14), "pi_3Nx0018", "2026-07-22T11:00", datos("stripe", "requires_confirmation", null))
        );
        transaccionRepository.saveAll(txs);
        log.info("[seed] Transaccion inserted: {}", txs.size());
    }

    private Pago pago(String monto, String fecha, EstadoPago estado, MetodoPago metodo,
                      EntidadOrigen entidad, int entidadRefId, Long idPaciente, String paymentIntentId) {
        return Pago.builder()
                .monto(new BigDecimal(monto))
                .fechaPago(LocalDateTime.parse(fecha))
                .estado(estado)
                .metodoPago(metodo)
                .entidadReferencia(entidad)
                .entidadReferenciaId(entidadRefId)
                .paymentIntentId(paymentIntentId)
                .idPaciente(idPaciente)
                .build();
    }

    private Factura factura(String numero, Pago pago, String fechaEmision,
                            String subtotal, String impuestos, String total) {
        return Factura.builder()
                .numeroFactura(numero)
                .pago(pago)
                .fechaEmision(LocalDateTime.parse(fechaEmision))
                .subtotal(new BigDecimal(subtotal))
                .impuestos(new BigDecimal(impuestos))
                .total(new BigDecimal(total))
                .build();
    }

    private Transaccion tx(Pago pago, String referenciaExterna, String fecha, Map<String, Object> datos) {
        return Transaccion.builder()
                .pago(pago)
                .referenciaExterna(referenciaExterna)
                .fechaTransaccion(LocalDateTime.parse(fecha))
                .datosTransaccion(datos)
                .build();
    }

    private Map<String, Object> datos(String gateway, String status, String brand) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("gateway", gateway);
        m.put("status", status);
        if (brand != null) {
            m.put("brand", brand);
        }
        return m;
    }
}
