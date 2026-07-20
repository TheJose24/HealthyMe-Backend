package studio.devbyjose.healthyme_reclamaciones.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import studio.devbyjose.healthyme_reclamaciones.entity.AdjuntoReclamacion;
import studio.devbyjose.healthyme_reclamaciones.entity.Reclamacion;
import studio.devbyjose.healthyme_reclamaciones.entity.RespuestaReclamacion;
import studio.devbyjose.healthyme_reclamaciones.entity.SeguimientoReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.CanalRecepcion;
import studio.devbyjose.healthyme_reclamaciones.enums.EstadoReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.PrioridadReclamacion;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoMotivo;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoRespuesta;
import studio.devbyjose.healthyme_reclamaciones.repository.ReclamacionRepository;

/**
 * Demo data seeder for healthyme-reclamaciones. Active only under the "seed" profile.
 * Children (respuestas, seguimientos, adjuntos) are persisted via cascade=ALL on the parent.
 * IDENTITY ids are never set manually; tables must be empty so auto-increment yields canonical ids.
 * fechaLimiteRespuesta is computed explicitly to avoid the @PrePersist NPE if @CreationTimestamp
 * has not yet populated fechaReclamacion.
 */
@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final ReclamacionRepository reclamacionRepository;

    @Override
    public void run(String... args) {
        if (reclamacionRepository.count() > 0) {
            log.info("[seed] reclamaciones already present, skipping");
            return;
        }

        List<Reclamacion> reclamaciones = new ArrayList<>();

        // ---- Reclamacion 1 (REC-2026-000001) ----
        Reclamacion r1 = base("REC-2026-000001", LocalDateTime.of(2026, 6, 14, 10, 0),
                TipoMotivo.RECLAMO, CanalRecepcion.WEB, EstadoReclamacion.EN_PROCESO,
                PrioridadReclamacion.ALTA, 18L, 10L,
                "Camila Reyes Ordoñez", "44000018", "camila.reyes@gmail.com", "+51 951000018",
                "Facturación de consulta", "Facturación", "admin",
                "Cobro duplicado en la consulta de dermatología");
        r1.setRespuestas(new ArrayList<>(List.of(
                respuesta(r1, TipoRespuesta.INICIAL, "admin", true, false,
                        "Hemos recibido su reclamo y estamos revisando el cobro duplicado."),
                respuesta(r1, TipoRespuesta.SOLICITUD_INFORMACION, "admin", true, false,
                        "Por favor adjunte el comprobante de pago para verificar."))));
        r1.setSeguimientos(new ArrayList<>(List.of(
                seguimiento(r1, EstadoReclamacion.RECIBIDO, EstadoReclamacion.EN_PROCESO,
                        "Asignación a Facturación", "admin", true, "Caso en revisión"))));
        r1.setAdjuntos(new ArrayList<>(List.of(
                adjunto(r1, "rec1_comprobante.pdf", "comprobante_pago.pdf", "application/pdf",
                        245678L, "/storage/reclam/1/comprobante.pdf", "camila.reyes", true),
                adjunto(r1, "rec1_captura.png", "captura_cobro.png", "image/png",
                        128900L, "/storage/reclam/1/captura.png", "camila.reyes", true))));
        reclamaciones.add(r1);

        // ---- Reclamacion 2 (REC-2026-000002) ----
        Reclamacion r2 = base("REC-2026-000002", LocalDateTime.of(2026, 6, 16, 9, 30),
                TipoMotivo.QUEJA, CanalRecepcion.PRESENCIAL, EstadoReclamacion.RESUELTO,
                PrioridadReclamacion.MEDIA, 5L, null,
                "Carlos Sánchez Rojas", "44000005", "carlos.sanchez@gmail.com", "+51 951000005",
                "Tiempo de espera", "Atención al cliente", "raguilar",
                "Espera de más de una hora para la cita");
        r2.setRespuestas(new ArrayList<>(List.of(
                respuesta(r2, TipoRespuesta.FINAL, "raguilar", true, true,
                        "Se ofrecen disculpas; se reforzó el aforo en horas pico."))));
        r2.setSeguimientos(new ArrayList<>(List.of(
                seguimiento(r2, EstadoReclamacion.RECIBIDO, EstadoReclamacion.RESUELTO,
                        "Cierre con disculpas", "raguilar", true, "Resuelto"))));
        reclamaciones.add(r2);

        // ---- Reclamacion 3 (REC-2026-000003) ----
        Reclamacion r3 = base("REC-2026-000003", LocalDateTime.of(2026, 6, 18, 11, 15),
                TipoMotivo.SUGERENCIA, CanalRecepcion.EMAIL, EstadoReclamacion.RECIBIDO,
                PrioridadReclamacion.BAJA, 7L, null,
                "Jorge Torres Paredes", "44000007", "jorge.torres@gmail.com", "+51 951000007",
                "Aplicación móvil", "Sistemas", "admin",
                "Sugiere agregar recordatorios por WhatsApp");
        reclamaciones.add(r3);

        // ---- Reclamacion 4 (REC-2026-000004) ----
        Reclamacion r4 = base("REC-2026-000004", LocalDateTime.of(2026, 6, 20, 8, 45),
                TipoMotivo.FELICITACION, CanalRecepcion.WEB, EstadoReclamacion.CERRADO,
                PrioridadReclamacion.BAJA, 2L, null,
                "Rosa Ramírez Díaz", "44000002", "rosa.ramirez@gmail.com", "+51 951000002",
                "Atención pediátrica", "Atención al cliente", "raguilar",
                "Felicita a la Dra. Fernández por su atención");
        r4.setRespuestas(new ArrayList<>(List.of(
                respuesta(r4, TipoRespuesta.FINAL, "raguilar", true, true,
                        "Gracias por su felicitación, la trasladamos al equipo."))));
        r4.setSeguimientos(new ArrayList<>(List.of(
                seguimiento(r4, EstadoReclamacion.RECIBIDO, EstadoReclamacion.CERRADO,
                        "Registro de felicitación", "raguilar", true, "Cerrado"))));
        reclamaciones.add(r4);

        // ---- Reclamacion 5 (REC-2026-000005) ----
        Reclamacion r5 = base("REC-2026-000005", LocalDateTime.of(2026, 6, 21, 14, 20),
                TipoMotivo.RECLAMO, CanalRecepcion.TELEFONO, EstadoReclamacion.PENDIENTE_INFORMACION,
                PrioridadReclamacion.CRITICA, 11L, 9L,
                "Fernando Gonzales Ríos", "44000011", "fernando.gonzales@gmail.com", "+51 951000011",
                "Resultado de examen", "Laboratorio", "dpalomino",
                "Resultado de perfil lipídico no entregado a tiempo");
        r5.setRespuestas(new ArrayList<>(List.of(
                respuesta(r5, TipoRespuesta.INICIAL, "dpalomino", true, false,
                        "Estamos ubicando su resultado con el laboratorio."),
                respuesta(r5, TipoRespuesta.SOLICITUD_INFORMACION, "dpalomino", false, false,
                        "Confirme su número de reserva de laboratorio."))));
        r5.setSeguimientos(new ArrayList<>(List.of(
                seguimiento(r5, EstadoReclamacion.RECIBIDO, EstadoReclamacion.EN_PROCESO,
                        "Escalado a Laboratorio", "dpalomino", true, "Prioridad crítica"),
                seguimiento(r5, EstadoReclamacion.EN_PROCESO, EstadoReclamacion.PENDIENTE_INFORMACION,
                        "Solicitud de datos al cliente", "dpalomino", true, "Esperando respuesta"))));
        r5.setAdjuntos(new ArrayList<>(List.of(
                adjunto(r5, "rec5_reserva.pdf", "reserva_laboratorio.pdf", "application/pdf",
                        98450L, "/storage/reclam/5/reserva.pdf", "fernando.gonzales", true))));
        reclamaciones.add(r5);

        // ---- Reclamacion 6 (REC-2026-000006) ----
        Reclamacion r6 = base("REC-2026-000006", LocalDateTime.of(2026, 6, 22, 10, 0),
                TipoMotivo.QUEJA, CanalRecepcion.EMAIL, EstadoReclamacion.ANULADO,
                PrioridadReclamacion.MEDIA, 19L, null,
                "Óscar Medina Vílchez", "44000019", "oscar.medina@gmail.com", "+51 951000019",
                "Cancelación de cita", "Atención al cliente", "admin",
                "Reclamación anulada — duplicada");
        r6.setSeguimientos(new ArrayList<>(List.of(
                seguimiento(r6, EstadoReclamacion.RECIBIDO, EstadoReclamacion.ANULADO,
                        "Anulación por duplicidad", "admin", false, "Duplicado de otro caso"))));
        reclamaciones.add(r6);

        reclamacionRepository.saveAll(reclamaciones);
        log.info("[seed] Inserted {} reclamaciones (with respuestas, seguimientos, adjuntos via cascade)",
                reclamaciones.size());
    }

    private Reclamacion base(String numero, LocalDateTime fecha, TipoMotivo tipoMotivo,
            CanalRecepcion canal, EstadoReclamacion estado, PrioridadReclamacion prioridad,
            Long idPaciente, Long idPago, String nombreReclamante, String dni, String email,
            String telefono, String servicio, String area, String asignadoA, String descripcion) {
        return Reclamacion.builder()
                .numeroReclamacion(numero)
                .fechaReclamacion(fecha)
                .fechaLimiteRespuesta(fecha.plusDays(prioridad.getDiasRespuesta()))
                .tipoMotivo(tipoMotivo)
                .canalRecepcion(canal)
                .estado(estado)
                .prioridad(prioridad)
                .requiereRespuesta(true)
                .idPaciente(idPaciente)
                .idCita(null) // UUID cita ids are incompatible with Long — left null by dataset contract
                .idPago(idPago)
                .nombreReclamante(nombreReclamante)
                .dniReclamante(dni)
                .emailReclamante(email)
                .telefonoReclamante(telefono)
                .servicioCriticado(servicio)
                .areaResponsable(area)
                .asignadoA(asignadoA)
                .createdBy(asignadoA)
                .descripcion(descripcion)
                .build();
    }

    private RespuestaReclamacion respuesta(Reclamacion reclamacion, TipoRespuesta tipo,
            String responsable, boolean notificado, boolean esFinal, String contenido) {
        return RespuestaReclamacion.builder()
                .reclamacion(reclamacion)
                .tipoRespuesta(tipo)
                .contenido(contenido)
                .responsable(responsable)
                .notificadoCliente(notificado)
                .esRespuestaFinal(esFinal)
                .build();
    }

    private SeguimientoReclamacion seguimiento(Reclamacion reclamacion, EstadoReclamacion anterior,
            EstadoReclamacion nuevo, String accion, String usuario, boolean visible, String comentario) {
        return SeguimientoReclamacion.builder()
                .reclamacion(reclamacion)
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .accionRealizada(accion)
                .usuarioResponsable(usuario)
                .esVisibleCliente(visible)
                .comentario(comentario)
                .build();
    }

    private AdjuntoReclamacion adjunto(Reclamacion reclamacion, String nombreArchivo,
            String nombreOriginal, String tipoContenido, Long tamano, String ruta,
            String subidoPor, boolean esEvidencia) {
        return AdjuntoReclamacion.builder()
                .reclamacion(reclamacion)
                .nombreArchivo(nombreArchivo)
                .nombreOriginal(nombreOriginal)
                .tipoContenido(tipoContenido)
                .tamanoArchivo(tamano)
                .rutaArchivo(ruta)
                .subidoPor(subidoPor)
                .esEvidencia(esEvidencia)
                .build();
    }
}
