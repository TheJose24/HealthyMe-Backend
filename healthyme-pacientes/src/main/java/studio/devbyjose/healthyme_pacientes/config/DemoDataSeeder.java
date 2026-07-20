package studio.devbyjose.healthyme_pacientes.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_pacientes.entity.HistorialMedico;
import studio.devbyjose.healthyme_pacientes.entity.Paciente;
import studio.devbyjose.healthyme_pacientes.entity.Seguro;
import studio.devbyjose.healthyme_pacientes.entity.Triaje;
import studio.devbyjose.healthyme_pacientes.repository.HistorialMedicoRepository;
import studio.devbyjose.healthyme_pacientes.repository.PacienteRepository;
import studio.devbyjose.healthyme_pacientes.repository.SeguroRepository;
import studio.devbyjose.healthyme_pacientes.repository.TriajeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo data seeder for healthyme-pacientes. Active only under the "seed" profile.
 * Seeds the canonical dataset: Seguro -> Paciente -> Triaje -> HistorialMedico.
 * IDENTITY ids are left to auto-increment; tables must be empty for canonical ids.
 */
@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final SeguroRepository seguroRepo;
    private final PacienteRepository pacienteRepo;
    private final TriajeRepository triajeRepo;
    private final HistorialMedicoRepository historialRepo;

    @Override
    public void run(String... args) {
        List<Seguro> seguros = seedSeguros();
        List<Paciente> pacientes = seedPacientes(seguros);
        List<Triaje> triajes = seedTriajes(pacientes);
        seedHistoriales(pacientes, triajes);
        log.info("[seed] pacientes demo data seeding complete");
    }

    private List<Seguro> seedSeguros() {
        if (seguroRepo.count() > 0) {
            log.info("[seed] seguros already present, skipping");
            return seguroRepo.findAll();
        }
        String[] nombres = {"EsSalud", "Rimac Seguros", "Pacifico Seguros", "SIS (Seguro Integral de Salud)"};
        List<Seguro> out = new ArrayList<>();
        for (String nombre : nombres) {
            Seguro s = new Seguro();
            s.setNombre(nombre);
            out.add(s);
        }
        List<Seguro> saved = seguroRepo.saveAll(out);
        log.info("[seed] inserted {} seguros", saved.size());
        return saved;
    }

    // Paciente: id_usuario 16..35, id_seguro per canonical plan (§4.2).
    private List<Paciente> seedPacientes(List<Seguro> seguros) {
        if (pacienteRepo.count() > 0) {
            log.info("[seed] pacientes already present, skipping");
            return pacienteRepo.findAll();
        }
        // {idUsuario, idSeguro(1-based)}
        int[][] rows = {
                {16, 1}, {17, 2}, {18, 3}, {19, 4}, {20, 1},
                {21, 2}, {22, 1}, {23, 3}, {24, 4}, {25, 2},
                {26, 1}, {27, 3}, {28, 2}, {29, 4}, {30, 1},
                {31, 3}, {32, 2}, {33, 1}, {34, 4}, {35, 4}
        };
        List<Paciente> out = new ArrayList<>();
        for (int[] r : rows) {
            Paciente p = new Paciente();
            p.setIdUsuario((long) r[0]);
            p.setSeguro(seguros.get(r[1] - 1));
            out.add(p);
        }
        List<Paciente> saved = pacienteRepo.saveAll(out);
        log.info("[seed] inserted {} pacientes", saved.size());
        return saved;
    }

    // Triaje: one per paciente (id_paciente = row index 1..20). All fields NOT NULL, decimals <= 999.99.
    private List<Triaje> seedTriajes(List<Paciente> pacientes) {
        if (triajeRepo.count() > 0) {
            log.info("[seed] triajes already present, skipping");
            return triajeRepo.findAll();
        }
        Object[][] rows = {
                // pacienteIdx(1-based), fecha, hora, peso, talla, alergias, condiciones, antecedentes, presion, fc, fr
                {1, "2026-06-01", "09:00", "78.50", "1.75", "Ninguna", "Hipertension leve", "Padre hipertenso", "130.00", "78.00", "16.00"},
                {2, "2026-06-02", "09:30", "62.00", "1.62", "Penicilina", "Ninguna", "Madre diabetica", "118.00", "72.00", "15.00"},
                {3, "2026-06-03", "10:00", "85.20", "1.80", "Ninguna", "Diabetes tipo 2", "Padre diabetico", "140.00", "82.00", "18.00"},
                {4, "2026-06-04", "08:45", "55.00", "1.58", "Mariscos", "Asma", "Ninguno", "110.00", "68.00", "17.00"},
                {5, "2026-06-05", "11:00", "90.00", "1.78", "Ninguna", "Obesidad", "Padre cardiopata", "135.00", "80.00", "16.00"},
                {6, "2026-06-06", "09:15", "50.50", "1.55", "Polen", "Ninguna", "Ninguno", "105.00", "70.00", "15.00"},
                {7, "2026-06-08", "10:30", "72.00", "1.70", "Ninguna", "Hipertension", "Madre hipertensa", "145.00", "85.00", "17.00"},
                {8, "2026-06-09", "08:30", "64.00", "1.60", "AINEs", "Migrana", "Ninguno", "120.00", "74.00", "16.00"},
                {9, "2026-06-10", "09:45", "80.00", "1.76", "Ninguna", "Ninguna", "Padre diabetico", "125.00", "76.00", "15.00"},
                {10, "2026-06-11", "11:15", "58.00", "1.63", "Ninguna", "Anemia", "Madre anemica", "112.00", "71.00", "16.00"},
                {11, "2026-06-12", "08:00", "88.00", "1.82", "Latex", "Colesterol alto", "Padre cardiopata", "138.00", "79.00", "17.00"},
                {12, "2026-06-13", "10:00", "60.00", "1.59", "Ninguna", "Hipotiroidismo", "Madre hipotiroidea", "115.00", "69.00", "15.00"},
                {13, "2026-06-15", "09:00", "75.00", "1.73", "Ninguna", "Ninguna", "Ninguno", "122.00", "73.00", "16.00"},
                {14, "2026-06-16", "09:30", "53.00", "1.60", "Polvo", "Asma", "Madre asmatica", "108.00", "67.00", "18.00"},
                {15, "2026-06-17", "10:45", "82.00", "1.74", "Ninguna", "Diabetes tipo 2", "Padre diabetico", "142.00", "83.00", "17.00"},
                {16, "2026-06-18", "08:15", "49.00", "1.54", "Ninguna", "Ninguna", "Ninguno", "100.00", "66.00", "15.00"},
                {17, "2026-06-19", "11:30", "77.00", "1.71", "Sulfamidas", "Gastritis", "Madre gastritis", "128.00", "77.00", "16.00"},
                {18, "2026-06-20", "09:00", "66.00", "1.65", "Ninguna", "Ninguna", "Ninguno", "118.00", "72.00", "15.00"},
                {19, "2026-06-22", "10:15", "95.00", "1.80", "Ninguna", "Hipertension, Obesidad", "Padre hipertenso", "150.00", "88.00", "19.00"},
                {20, "2026-06-23", "08:30", "57.00", "1.61", "Ninguna", "Ninguna", "Madre diabetica", "114.00", "70.00", "16.00"}
        };
        List<Triaje> out = new ArrayList<>();
        for (Object[] r : rows) {
            Triaje t = new Triaje();
            t.setPaciente(pacientes.get((int) r[0] - 1));
            t.setFecha(LocalDate.parse((String) r[1]));
            t.setHora(LocalTime.parse((String) r[2]));
            t.setPeso(new BigDecimal((String) r[3]));
            t.setTalla(new BigDecimal((String) r[4]));
            t.setAlergias((String) r[5]);
            t.setCondicionesMedicas((String) r[6]);
            t.setAntecedentesFamiliares((String) r[7]);
            t.setPresionArterial(new BigDecimal((String) r[8]));
            t.setFrecuenciaCardiaca(new BigDecimal((String) r[9]));
            t.setFrecuenciaRespiratoria(new BigDecimal((String) r[10]));
            out.add(t);
        }
        List<Triaje> saved = triajeRepo.saveAll(out);
        log.info("[seed] inserted {} triajes", saved.size());
        return saved;
    }

    // HistorialMedico (§4.4): {pacienteIdx, triajeIdx, idConsulta} — both idx are 1-based canonical ids.
    private void seedHistoriales(List<Paciente> pacientes, List<Triaje> triajes) {
        if (historialRepo.count() > 0) {
            log.info("[seed] historiales already present, skipping");
            return;
        }
        int[][] rows = {
                {1, 1, 1}, {2, 2, 2}, {3, 3, 3}, {4, 4, 4}, {5, 5, 5},
                {6, 6, 6}, {7, 7, 7}, {8, 8, 8}, {9, 9, 9}, {10, 10, 10},
                {11, 11, 11}, {12, 12, 12}, {13, 13, 13}, {15, 15, 14}, {17, 17, 15}
        };
        List<HistorialMedico> out = new ArrayList<>();
        for (int[] r : rows) {
            HistorialMedico h = new HistorialMedico();
            h.setPaciente(pacientes.get(r[0] - 1));
            h.setTriaje(triajes.get(r[1] - 1));
            h.setIdConsulta((long) r[2]);
            out.add(h);
        }
        List<HistorialMedico> saved = historialRepo.saveAll(out);
        log.info("[seed] inserted {} historiales medicos", saved.size());
    }
}
