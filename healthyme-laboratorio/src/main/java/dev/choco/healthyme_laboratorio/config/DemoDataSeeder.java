package dev.choco.healthyme_laboratorio.config;

import dev.choco.healthyme_laboratorio.entity.Examen;
import dev.choco.healthyme_laboratorio.entity.ReservaLab;
import dev.choco.healthyme_laboratorio.enums.EstadoReserva;
import dev.choco.healthyme_laboratorio.repository.ExamenRepository;
import dev.choco.healthyme_laboratorio.repository.ReservaLabRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo-data seeder for healthyme-laboratorio (DB healthyme_laboratorio).
 * Enabled only under the "seed" profile. Seeds ReservaLab (12 rows) then Examen (15 rows).
 * IDs are left null so MySQL IDENTITY yields the canonical 1..N in insertion order.
 * Timestamps (fechaCreacion/ultimaModificacion) are left null; Hibernate @CreationTimestamp/
 * @UpdateTimestamp populate them on persist.
 */
@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final ReservaLabRepository reservaLabRepository;
    private final ExamenRepository examenRepository;

    @Override
    public void run(String... args) {
        if (reservaLabRepository.count() > 0 || examenRepository.count() > 0) {
            log.info("[seed] laboratorio data already present, skipping");
            return;
        }

        // --- ReservaLab (ids 1..12 by insertion order) ---
        List<ReservaLab> reservas = new ArrayList<>();
        reservas.add(reserva("2026-06-01", "07:30", EstadoReserva.REALIZADA, 1, 1, 1));
        reservas.add(reserva("2026-06-03", "08:00", EstadoReserva.REALIZADA, 3, 1, 1));
        reservas.add(reserva("2026-06-05", "07:45", EstadoReserva.REALIZADA, 5, 1, 3));
        reservas.add(reserva("2026-06-08", "08:15", EstadoReserva.REALIZADA, 7, 1, 1));
        reservas.add(reserva("2026-06-09", "09:00", EstadoReserva.REALIZADA, 8, 2, 2));
        reservas.add(reserva("2026-06-11", "07:30", EstadoReserva.REALIZADA, 10, 1, 1));
        reservas.add(reserva("2026-06-12", "08:30", EstadoReserva.REALIZADA, 11, 1, 3));
        reservas.add(reserva("2026-06-15", "09:15", EstadoReserva.REALIZADA, 13, 3, 4));
        reservas.add(reserva("2026-06-19", "07:45", EstadoReserva.REALIZADA, 17, 1, 1));
        reservas.add(reserva("2026-07-21", "08:00", EstadoReserva.PENDIENTE, 1, 1, 1));
        reservas.add(reserva("2026-07-22", "08:30", EstadoReserva.PENDIENTE, 9, 2, 2));
        reservas.add(reserva("2026-06-10", "09:00", EstadoReserva.CANCELADA, 6, 1, 3));

        // saveAll preserves list order -> ids 1..12
        List<ReservaLab> savedReservas = reservaLabRepository.saveAll(reservas);
        log.info("[seed] inserted {} reserva_lab rows", savedReservas.size());

        // --- Examen (ids 1..15). reservaIdx = 1-based reserva id from the plan. ---
        List<Examen> examenes = new ArrayList<>();
        examenes.add(examen("Hemograma completo",       savedReservas.get(0),  "2026-06-01", 1, 1, 1,  "Valores normales",        "—"));
        examenes.add(examen("Perfil lipídico",          savedReservas.get(0),  "2026-06-01", 1, 1, 1,  "Colesterol 210 mg/dL",    "Ligeramente elevado"));
        examenes.add(examen("Glucosa en ayunas",        savedReservas.get(1),  "2026-06-03", 1, 1, 3,  "145 mg/dL",               "Compatible con diabetes"));
        examenes.add(examen("Electrocardiograma",       savedReservas.get(2),  "2026-06-05", 3, 1, 5,  "Ritmo sinusal normal",    "—"));
        examenes.add(examen("Hemograma completo",       savedReservas.get(3),  "2026-06-08", 1, 1, 7,  "Valores normales",        "—"));
        examenes.add(examen("Radiografía de tórax",     savedReservas.get(4),  "2026-06-09", 2, 2, 8,  "Sin alteraciones",        "—"));
        examenes.add(examen("Hemograma completo",       savedReservas.get(5),  "2026-06-11", 1, 1, 10, "Hemoglobina 10.2 g/dL",   "Anemia leve"));
        examenes.add(examen("Perfil lipídico",          savedReservas.get(6),  "2026-06-12", 3, 1, 11, "Colesterol 240 mg/dL",    "Dislipidemia"));
        examenes.add(examen("Biopsia de piel",          savedReservas.get(7),  "2026-06-15", 4, 3, 13, "Benigno",                 "—"));
        examenes.add(examen("Perfil tiroideo",          savedReservas.get(7),  "2026-06-15", 4, 3, 13, "TSH normal",              "—"));
        examenes.add(examen("Examen de orina",          savedReservas.get(8),  "2026-06-19", 1, 1, 17, "Normal",                  "—"));
        examenes.add(examen("Glucosa en ayunas",        savedReservas.get(8),  "2026-06-19", 1, 1, 17, "98 mg/dL",                "Normal"));
        examenes.add(examen("Hemoglobina glicosilada", savedReservas.get(1),  "2026-06-03", 1, 1, 3,  "HbA1c 7.8%",              "Control diabético"));
        examenes.add(examen("Ecografía abdominal",      savedReservas.get(4),  "2026-06-09", 2, 2, 8,  "Sin hallazgos",           "—"));
        examenes.add(examen("Perfil hepático",          savedReservas.get(3),  "2026-06-08", 1, 1, 7,  "Normal",                  "—"));

        examenRepository.saveAll(examenes);
        log.info("[seed] inserted {} examenes rows", examenes.size());
        log.info("[seed] laboratorio demo data seeding complete");
    }

    private static ReservaLab reserva(String fecha, String hora, EstadoReserva estado,
                                      int idPaciente, int idTecnico, int idLaboratorio) {
        ReservaLab r = new ReservaLab();
        r.setFecha(LocalDate.parse(fecha));
        r.setHora(LocalTime.parse(hora));
        r.setEstado(estado);
        r.setIdPaciente(idPaciente);
        r.setIdTecnico(idTecnico);
        r.setIdLaboratorio(idLaboratorio);
        return r;
    }

    private static Examen examen(String nombreExamen, ReservaLab reservaLab, String fechaRealizacion,
                                 int idLaboratorio, int idTecnico, int idPaciente,
                                 String resultados, String observaciones) {
        Examen e = new Examen();
        e.setNombreExamen(nombreExamen);
        e.setReservaLab(reservaLab);
        e.setFechaRealizacion(LocalDate.parse(fechaRealizacion));
        e.setIdLaboratorio(idLaboratorio);
        e.setIdTecnico(idTecnico);
        e.setIdPaciente(idPaciente);
        e.setResultados(resultados);
        e.setObservaciones(observaciones);
        return e;
    }
}
