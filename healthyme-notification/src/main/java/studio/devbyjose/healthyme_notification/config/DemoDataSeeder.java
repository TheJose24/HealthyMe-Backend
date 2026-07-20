package studio.devbyjose.healthyme_notification.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.notification.EstadoNotificacion;
import studio.devbyjose.healthyme_notification.entity.Adjunto;
import studio.devbyjose.healthyme_notification.entity.ConfiguracionNotificacion;
import studio.devbyjose.healthyme_notification.entity.EventoPendiente;
import studio.devbyjose.healthyme_notification.entity.Notificacion;
import studio.devbyjose.healthyme_notification.entity.Plantilla;
import studio.devbyjose.healthyme_notification.enums.TipoPlantilla;
import studio.devbyjose.healthyme_notification.repository.AdjuntoRepository;
import studio.devbyjose.healthyme_notification.repository.EventoPendienteRepository;
import studio.devbyjose.healthyme_notification.repository.NotificacionRepository;
import studio.devbyjose.healthyme_notification.repository.PlantillaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo data seeder for healthyme-notification (MySQL healthyme_notification).
 * Active only under the "seed" profile. Seeds into empty tables in insert order
 * so IDENTITY auto-increment yields the canonical ids from the dataset plan.
 */
@Slf4j
@Component
@Profile("seed")
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final PlantillaRepository plantillaRepo;
    private final NotificacionRepository notificacionRepo;
    private final AdjuntoRepository adjuntoRepo;
    private final EventoPendienteRepository eventoPendienteRepo;

    // ConfiguracionNotificacionRepository does NOT extend JpaRepository (empty
    // interface) -> persist configuracion_notificacion via EntityManager.
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        List<Plantilla> plantillas = seedPlantillas();
        List<Notificacion> notificaciones = seedNotificaciones(plantillas);
        seedAdjuntos(notificaciones);
        seedConfiguraciones();
        seedEventosPendientes();
        log.info("[seed] healthyme-notification demo data seeding complete");
    }

    // 9.1 Plantilla — ids 1–5
    private List<Plantilla> seedPlantillas() {
        if (plantillaRepo.count() > 0) {
            log.info("[seed] plantilla already present, skipping");
            return plantillaRepo.findAll();
        }
        List<Plantilla> list = new ArrayList<>();
        list.add(Plantilla.builder().tipo(TipoPlantilla.EMAIL).nombre("cita_confirmada")
                .asunto("Confirmación de su cita médica")
                .variables("[\"nombre\",\"fecha\",\"hora\",\"medico\"]").build());
        list.add(Plantilla.builder().tipo(TipoPlantilla.EMAIL).nombre("resultado_examen")
                .asunto("Resultados de laboratorio disponibles")
                .variables("[\"nombre\",\"examen\",\"fecha\"]").build());
        list.add(Plantilla.builder().tipo(TipoPlantilla.SMS).nombre("recordatorio_cita")
                .asunto(null)
                .variables("[\"nombre\",\"fecha\",\"hora\"]").build());
        list.add(Plantilla.builder().tipo(TipoPlantilla.EMAIL).nombre("pago_confirmado")
                .asunto("Comprobante de pago")
                .variables("[\"nombre\",\"monto\",\"numeroFactura\"]").build());
        list.add(Plantilla.builder().tipo(TipoPlantilla.PUSH).nombre("reclamacion_actualizada")
                .asunto("Actualización de su reclamación")
                .variables("[\"nombre\",\"numeroReclamacion\",\"estado\"]").build());
        List<Plantilla> saved = plantillaRepo.saveAll(list);
        log.info("[seed] inserted {} plantillas", saved.size());
        return saved;
    }

    // 9.2 Notificacion — ids 1–15 (fechaEnvio auto via @CreationTimestamp)
    private List<Notificacion> seedNotificaciones(List<Plantilla> p) {
        if (notificacionRepo.count() > 0) {
            log.info("[seed] notificacion already present, skipping");
            return notificacionRepo.findAll();
        }
        List<Notificacion> list = new ArrayList<>();
        list.add(notif("juan.perez@gmail.com", EstadoNotificacion.ENVIADO, p.get(0), EntidadOrigen.CONSULTA, 1,
                "{\"nombre\":\"Juan\",\"fecha\":\"2026-06-01\",\"medico\":\"Dr. Quispe\"}"));
        list.add(notif("rosa.ramirez@gmail.com", EstadoNotificacion.ENVIADO, p.get(0), EntidadOrigen.CONSULTA, 2,
                "{\"nombre\":\"Rosa\",\"fecha\":\"2026-06-02\",\"medico\":\"Dra. Fernández\"}"));
        list.add(notif("luis.garcia@gmail.com", EstadoNotificacion.ENVIADO, p.get(1), EntidadOrigen.EXAMEN, 3,
                "{\"nombre\":\"Luis\",\"examen\":\"Glucosa\"}"));
        list.add(notif("ana.rodriguez@gmail.com", EstadoNotificacion.ENVIADO, p.get(0), EntidadOrigen.CONSULTA, 4,
                "{\"nombre\":\"Ana\",\"fecha\":\"2026-06-04\",\"medico\":\"Dra. Ríos\"}"));
        list.add(notif("carlos.sanchez@gmail.com", EstadoNotificacion.ENVIADO, p.get(3), EntidadOrigen.PAGO, 5,
                "{\"nombre\":\"Carlos\",\"monto\":\"100.00\",\"numeroFactura\":\"F001-00000005\"}"));
        list.add(notif("elena.flores@gmail.com", EstadoNotificacion.ENVIADO, p.get(1), EntidadOrigen.EXAMEN, 6,
                "{\"nombre\":\"Elena\",\"examen\":\"Radiografía\"}"));
        list.add(notif("jorge.torres@gmail.com", EstadoNotificacion.ENVIADO, p.get(3), EntidadOrigen.PAGO, 6,
                "{\"nombre\":\"Jorge\",\"monto\":\"90.00\",\"numeroFactura\":\"F001-00000006\"}"));
        list.add(notif("miguel.diaz@gmail.com", EstadoNotificacion.ENVIADO, p.get(0), EntidadOrigen.CONSULTA, 8,
                "{\"nombre\":\"Miguel\",\"fecha\":\"2026-06-09\",\"medico\":\"Dr. Ramos\"}"));
        list.add(notif("sofia.vargas@gmail.com", EstadoNotificacion.PENDIENTE, p.get(1), EntidadOrigen.EXAMEN, 7,
                "{\"nombre\":\"Sofía\",\"examen\":\"Hemograma\"}"));
        list.add(notif("fernando.gonzales@gmail.com", EstadoNotificacion.ENVIADO, p.get(3), EntidadOrigen.PAGO, 9,
                "{\"nombre\":\"Fernando\",\"monto\":\"150.00\",\"numeroFactura\":\"F001-00000009\"}"));
        list.add(notif("patricia.mendoza@gmail.com", EstadoNotificacion.ENVIADO, p.get(0), EntidadOrigen.CONSULTA, 12,
                "{\"nombre\":\"Patricia\",\"fecha\":\"2026-06-13\",\"medico\":\"Dra. Torres\"}"));
        list.add(notif("juan.perez@gmail.com", EstadoNotificacion.PENDIENTE, p.get(2), EntidadOrigen.CONSULTA, 1,
                "{\"nombre\":\"Juan\",\"fecha\":\"2026-07-21\",\"hora\":\"09:00\"}"));
        list.add(notif("daniel.salazar@gmail.com", EstadoNotificacion.ENVIADO, p.get(1), EntidadOrigen.EXAMEN, 11,
                "{\"nombre\":\"Daniel\",\"examen\":\"Orina\"}"));
        list.add(notif("ricardo.herrera@gmail.com", EstadoNotificacion.ERROR, p.get(3), EntidadOrigen.PAGO, 10,
                "{\"nombre\":\"Ricardo\",\"monto\":\"130.00\",\"numeroFactura\":\"F001-00000010\"}"));
        list.add(notif("camila.reyes@gmail.com", EstadoNotificacion.ENVIADO, p.get(4), EntidadOrigen.RECLAMACIONES, 1,
                "{\"nombre\":\"Camila\",\"numeroReclamacion\":\"REC-2026-000001\",\"estado\":\"EN_PROCESO\"}"));
        List<Notificacion> saved = notificacionRepo.saveAll(list);
        log.info("[seed] inserted {} notificaciones", saved.size());
        return saved;
    }

    private Notificacion notif(String dest, EstadoNotificacion estado, Plantilla plantilla,
                               EntidadOrigen origen, Integer idOrigen, String datosContexto) {
        return Notificacion.builder()
                .destinatario(dest)
                .estado(estado)
                .plantilla(plantilla)
                .entidadOrigen(origen)
                .idOrigen(idOrigen)
                .datosContexto(datosContexto)
                .build();
    }

    // 9.3 Adjunto — ids 1–5 (notificacion FK: positions 3,5,6,10,13 -> 0-based 2,4,5,9,12)
    private void seedAdjuntos(List<Notificacion> n) {
        if (adjuntoRepo.count() > 0) {
            log.info("[seed] adjunto already present, skipping");
            return;
        }
        List<Adjunto> list = new ArrayList<>();
        list.add(adjunto(n.get(2), "resultado_glucosa_luis.pdf", "application/pdf",
                "/storage/notif/3/resultado_glucosa.pdf"));
        list.add(adjunto(n.get(4), "factura_F001-00000005.pdf", "application/pdf",
                "/storage/notif/5/factura.pdf"));
        list.add(adjunto(n.get(5), "radiografia_elena.pdf", "application/pdf",
                "/storage/notif/6/radiografia.pdf"));
        list.add(adjunto(n.get(9), "factura_F001-00000009.pdf", "application/pdf",
                "/storage/notif/10/factura.pdf"));
        list.add(adjunto(n.get(12), "resultado_orina_daniel.pdf", "application/pdf",
                "/storage/notif/13/resultado_orina.pdf"));
        adjuntoRepo.saveAll(list);
        log.info("[seed] inserted {} adjuntos", list.size());
    }

    private Adjunto adjunto(Notificacion notif, String nombre, String tipo, String url) {
        return Adjunto.builder()
                .notificacion(notif)
                .nombre(nombre)
                .tipoContenido(tipo)
                .urlAlmacenamiento(url)
                .build();
    }

    // 9.4 ConfiguracionNotificacion — ids 1–20 (id_usuario 16..35, unique)
    private void seedConfiguraciones() {
        Long existing = em.createQuery(
                "SELECT COUNT(c) FROM ConfiguracionNotificacion c", Long.class).getSingleResult();
        if (existing != null && existing > 0) {
            log.info("[seed] configuracion_notificacion already present, skipping");
            return;
        }
        // Overrides for users 16..20; users 21..35 use defaults (email=true, sms=false, push=true).
        for (int idUsuario = 16; idUsuario <= 35; idUsuario++) {
            ConfiguracionNotificacion.ConfiguracionNotificacionBuilder b =
                    ConfiguracionNotificacion.builder().idUsuario(idUsuario);
            switch (idUsuario) {
                case 16 -> b.recibirEmail(true).recibirSms(true).recibirPush(true);
                case 17 -> b.recibirEmail(true).recibirSms(false).recibirPush(true);
                case 18 -> b.recibirEmail(true).recibirSms(false).recibirPush(false);
                case 19 -> b.recibirEmail(true).recibirSms(true).recibirPush(true);
                case 20 -> b.recibirEmail(true).recibirSms(false).recibirPush(true);
                default -> b.recibirEmail(true).recibirSms(false).recibirPush(true);
            }
            em.persist(b.build());
        }
        log.info("[seed] inserted 20 configuraciones_notificacion");
    }

    // 9.5 EventoPendiente — ids 1–5 (all non-null fields set explicitly)
    private void seedEventosPendientes() {
        if (eventoPendienteRepo.count() > 0) {
            log.info("[seed] eventos_pendientes already present, skipping");
            return;
        }
        List<EventoPendiente> list = new ArrayList<>();
        list.add(EventoPendiente.builder()
                .tipo("CITA").tipoEvento("CREACION").idEntidad(16)
                .destinatario("juan.perez@gmail.com")
                .fechaCreacion(LocalDateTime.of(2026, 7, 20, 8, 0))
                .fechaProximoIntento(LocalDateTime.of(2026, 7, 20, 8, 5))
                .intentos(0).procesado(false)
                .datosEvento("{\"idCita\":\"00000000-0000-0000-0000-000000000016\",\"fecha\":\"2026-07-21\"}")
                .mensajeError(null)
                .build());
        list.add(EventoPendiente.builder()
                .tipo("EXAMEN").tipoEvento("ACTUALIZACION").idEntidad(9)
                .destinatario("sofia.vargas@gmail.com")
                .fechaCreacion(LocalDateTime.of(2026, 7, 20, 8, 10))
                .fechaProximoIntento(LocalDateTime.of(2026, 7, 20, 8, 15))
                .intentos(1).procesado(false)
                .datosEvento("{\"idExamen\":9,\"estado\":\"REALIZADA\"}")
                .mensajeError("Timeout SMTP")
                .build());
        list.add(EventoPendiente.builder()
                .tipo("RECETA").tipoEvento("CREACION").idEntidad(11)
                .destinatario("daniel.salazar@gmail.com")
                .fechaCreacion(LocalDateTime.of(2026, 7, 20, 8, 20))
                .fechaProximoIntento(LocalDateTime.of(2026, 7, 20, 8, 25))
                .intentos(0).procesado(false)
                .datosEvento("{\"idReceta\":11}")
                .mensajeError(null)
                .build());
        list.add(EventoPendiente.builder()
                .tipo("CITA").tipoEvento("ACTUALIZACION").idEntidad(27)
                .destinatario("rosa.ramirez@gmail.com")
                .fechaCreacion(LocalDateTime.of(2026, 6, 5, 14, 5))
                .fechaProximoIntento(LocalDateTime.of(2026, 6, 5, 14, 10))
                .intentos(2).procesado(true)
                .datosEvento("{\"idCita\":\"00000000-0000-0000-0000-000000000027\",\"estado\":\"CANCELADA\"}")
                .mensajeError(null)
                .build());
        list.add(EventoPendiente.builder()
                .tipo("EXAMEN").tipoEvento("CREACION").idEntidad(6)
                .destinatario("elena.flores@gmail.com")
                .fechaCreacion(LocalDateTime.of(2026, 6, 9, 9, 5))
                .fechaProximoIntento(LocalDateTime.of(2026, 6, 9, 9, 10))
                .intentos(0).procesado(true)
                .datosEvento("{\"idExamen\":6}")
                .mensajeError(null)
                .build());
        eventoPendienteRepo.saveAll(list);
        log.info("[seed] inserted {} eventos_pendientes", list.size());
    }
}
