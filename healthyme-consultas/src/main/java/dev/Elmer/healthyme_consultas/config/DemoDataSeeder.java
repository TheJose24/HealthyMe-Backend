package dev.Elmer.healthyme_consultas.config;

import dev.Elmer.healthyme_consultas.entity.Consulta;
import dev.Elmer.healthyme_consultas.entity.Medicamento;
import dev.Elmer.healthyme_consultas.entity.Receta;
import dev.Elmer.healthyme_consultas.repository.ConsultaRepository;
import dev.Elmer.healthyme_consultas.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Seeds demo data for the healthyme-consultas service (MySQL healthyme_consultas).
 * Enabled only under the "seed" Spring profile. Idempotent: skips per-aggregate when data exists.
 *
 * Insert order (FK: receta.id_consulta NOT NULL): Consulta -> Receta (with cascaded Medicamento).
 * IDENTITY ids are left null so seeding empty tables yields the canonical ids 1..15 / 1..12.
 * Cross-service scalar refs (idCita String UUID, idPaciente, idMedico) match the canonical dataset;
 * no FK constraints enforce them here. Timestamps are Hibernate-managed (do not set manually).
 */
@Slf4j
@Component
@Profile("seed")
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final ConsultaRepository consultaRepo;
    private final RecetaRepository recetaRepo;

    // Deterministic cita UUID for consulta n (n=1..15): "...0000" + zero-padded n.
    private static String citaUuid(int n) {
        return String.format("00000000-0000-0000-0000-0000000000%02d", n);
    }

    @Override
    public void run(String... args) {
        seedConsultas();
        seedRecetas();
    }

    private void seedConsultas() {
        if (consultaRepo.count() > 0) {
            log.info("[seed] consulta already present, skipping");
            return;
        }
        // {consultaIndex(1-based), idPaciente, idMedico, fecha, sintomas, diagnostico}
        Object[][] rows = {
                {1, 1, 1, "2026-06-01", "Dolor torácico y fatiga", "Hipertensión arterial estadio 1"},
                {2, 2, 2, "2026-06-02", "Fiebre y tos en menor", "Infección respiratoria alta"},
                {3, 3, 1, "2026-06-03", "Palpitaciones", "Arritmia leve en estudio"},
                {4, 4, 4, "2026-06-04", "Control ginecológico", "Chequeo preventivo normal"},
                {5, 5, 6, "2026-06-05", "Malestar general", "Cuadro gripal"},
                {6, 6, 2, "2026-06-06", "Rinitis alérgica", "Rinitis alérgica estacional"},
                {7, 7, 1, "2026-06-08", "Presión elevada", "Hipertensión arterial"},
                {8, 8, 7, "2026-06-09", "Cefalea intensa", "Migraña con aura"},
                {9, 9, 6, "2026-06-10", "Chequeo general", "Estado de salud normal"},
                {10, 10, 6, "2026-06-11", "Cansancio y palidez", "Anemia ferropénica"},
                {11, 11, 1, "2026-06-12", "Colesterol alto", "Dislipidemia"},
                {12, 12, 3, "2026-06-13", "Manchas en la piel", "Dermatitis de contacto"},
                {13, 13, 6, "2026-06-15", "Control anual", "Sin hallazgos patológicos"},
                {14, 15, 5, "2026-06-17", "Dolor de rodilla", "Gonartrosis leve"},
                {15, 17, 6, "2026-06-19", "Dolor abdominal", "Gastritis"},
        };
        List<Consulta> consultas = new ArrayList<>();
        for (Object[] r : rows) {
            Consulta c = new Consulta();
            c.setIdCita(citaUuid((int) r[0]));
            c.setIdPaciente((int) r[1]);
            c.setIdMedico((int) r[2]);
            c.setFecha(LocalDate.parse((String) r[3]));
            c.setSintomas((String) r[4]);
            c.setDiagnostico((String) r[5]);
            consultas.add(c);
        }
        consultaRepo.saveAll(consultas);
        log.info("[seed] inserted {} consultas", consultas.size());
    }

    private void seedRecetas() {
        if (recetaRepo.count() > 0) {
            log.info("[seed] receta already present, skipping");
            return;
        }
        // Consultas must already be seeded to attach the required @ManyToOne.
        List<Consulta> allConsultas = consultaRepo.findAll();
        if (allConsultas.isEmpty()) {
            log.warn("[seed] no consultas found, cannot seed recetas");
            return;
        }
        // Map consulta id -> entity for FK attachment.
        java.util.Map<Integer, Consulta> byId = new java.util.HashMap<>();
        for (Consulta c : allConsultas) {
            byId.put(c.getIdConsulta(), c);
        }

        // {recetaId(insert order), id_consulta, fecha_emision}
        Object[][] recetaRows = {
                {1, 1, "2026-06-01"},
                {2, 2, "2026-06-02"},
                {3, 5, "2026-06-05"},
                {4, 6, "2026-06-06"},
                {5, 7, "2026-06-08"},
                {6, 8, "2026-06-09"},
                {7, 10, "2026-06-11"},
                {8, 11, "2026-06-12"},
                {9, 12, "2026-06-13"},
                {10, 14, "2026-06-17"},
                {11, 15, "2026-06-19"},
                {12, 3, "2026-06-03"},
        };

        List<Receta> recetas = new ArrayList<>();
        for (Object[] r : recetaRows) {
            int recetaId = (int) r[0];
            int idConsulta = (int) r[1];
            Consulta consulta = byId.get(idConsulta);
            if (consulta == null) {
                log.warn("[seed] missing consulta {} for receta {}, skipping", idConsulta, recetaId);
                continue;
            }
            Receta receta = new Receta();
            receta.setFechaEmision(LocalDate.parse((String) r[2]));
            receta.setConsulta(consulta);
            receta.setMedicamentos(medicamentosFor(recetaId));
            recetas.add(receta);
        }
        recetaRepo.saveAll(recetas);
        log.info("[seed] inserted {} recetas with cascaded medicamentos", recetas.size());
    }

    // Medicamentos keyed by receta insert-order id (1..12), cascaded via Receta.medicamentos.
    private List<Medicamento> medicamentosFor(int recetaId) {
        switch (recetaId) {
            case 1:
                return Arrays.asList(
                        new Medicamento("Losartán 50mg", "1 tab c/24h", "Tomar en la mañana"),
                        new Medicamento("Aspirina 100mg", "1 tab c/24h", "Después del desayuno"));
            case 2:
                return Arrays.asList(
                        new Medicamento("Paracetamol 500mg", "1 tab c/8h", "Si fiebre >38°C"),
                        new Medicamento("Amoxicilina 500mg", "1 cap c/8h", "Por 7 días"));
            case 3:
                return Arrays.asList(
                        new Medicamento("Paracetamol 500mg", "1 tab c/8h", "Por 3 días"));
            case 4:
                return Arrays.asList(
                        new Medicamento("Loratadina 10mg", "1 tab c/24h", "En la noche"));
            case 5:
                return Arrays.asList(
                        new Medicamento("Enalapril 10mg", "1 tab c/12h", "Controlar presión"));
            case 6:
                return Arrays.asList(
                        new Medicamento("Sumatriptán 50mg", "1 tab en crisis", "Máx 2/día"));
            case 7:
                return Arrays.asList(
                        new Medicamento("Sulfato ferroso 300mg", "1 tab c/24h", "Con jugo de naranja"));
            case 8:
                return Arrays.asList(
                        new Medicamento("Atorvastatina 20mg", "1 tab c/24h", "En la noche"));
            case 9:
                return Arrays.asList(
                        new Medicamento("Betametasona crema", "Aplicar c/12h", "Zona afectada"));
            case 10:
                return Arrays.asList(
                        new Medicamento("Ibuprofeno 400mg", "1 tab c/8h", "Con alimentos"));
            case 11:
                return Arrays.asList(
                        new Medicamento("Omeprazol 20mg", "1 cap c/24h", "En ayunas"));
            case 12:
                return Arrays.asList(
                        new Medicamento("Bisoprolol 5mg", "1 tab c/24h", "En la mañana"));
            default:
                return new ArrayList<>();
        }
    }
}
