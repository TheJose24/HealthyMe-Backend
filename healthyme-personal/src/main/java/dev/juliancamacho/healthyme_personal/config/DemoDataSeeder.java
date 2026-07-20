package dev.juliancamacho.healthyme_personal.config;

import dev.juliancamacho.healthyme_personal.entity.Enfermero;
import dev.juliancamacho.healthyme_personal.entity.Especialidad;
import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;
import dev.juliancamacho.healthyme_personal.entity.Medico;
import dev.juliancamacho.healthyme_personal.entity.Tecnico;
import dev.juliancamacho.healthyme_personal.entity.Unidad;
import dev.juliancamacho.healthyme_personal.enums.DiaSemana;
import dev.juliancamacho.healthyme_personal.repository.EnfermeroRepository;
import dev.juliancamacho.healthyme_personal.repository.EspecialidadRepository;
import dev.juliancamacho.healthyme_personal.repository.HorarioTrabajoRepository;
import dev.juliancamacho.healthyme_personal.repository.MedicoRepository;
import dev.juliancamacho.healthyme_personal.repository.TecnicoRepository;
import dev.juliancamacho.healthyme_personal.repository.UnidadRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo dataset seeder for healthyme-personal (MySQL healthyme_personal).
 * Active only under the "seed" Spring profile. Idempotent: skips when data exists.
 *
 * Insert order (FK deps): Especialidad, Unidad, HorarioTrabajo -> Medico, Tecnico,
 * Enfermero -> HorarioMedico, HorarioTecnico. IDENTITY ids are never set manually;
 * tables are seeded empty so auto-increment yields the canonical ids 1..N.
 *
 * The two join entities (HorarioMedico / HorarioTecnico) use a NON-static inner
 * @EmbeddedId that Hibernate cannot instantiate via save(); their rows are inserted
 * with native SQL against the join tables instead.
 */
@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final EspecialidadRepository especialidadRepo;
    private final UnidadRepository unidadRepo;
    private final HorarioTrabajoRepository horarioTrabajoRepo;
    private final MedicoRepository medicoRepo;
    private final TecnicoRepository tecnicoRepo;
    private final EnfermeroRepository enfermeroRepo;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        if (especialidadRepo.count() > 0) {
            log.info("[seed] healthyme-personal already seeded, skipping");
            return;
        }

        List<Especialidad> especialidades = seedEspecialidades();
        List<Unidad> unidades = seedUnidades();
        List<HorarioTrabajo> horarios = seedHorarios();

        List<Medico> medicos = seedMedicos(especialidades);
        List<Tecnico> tecnicos = seedTecnicos(unidades);
        seedEnfermeros(horarios);

        seedHorarioMedico(medicos, horarios);
        seedHorarioTecnico(tecnicos, horarios);

        log.info("[seed] healthyme-personal seeding complete");
    }

    private List<Especialidad> seedEspecialidades() {
        String[][] data = {
                {"Cardiología", "cardiologia.png"},
                {"Pediatría", "pediatria.png"},
                {"Dermatología", "dermatologia.png"},
                {"Ginecología", "ginecologia.png"},
                {"Traumatología", "traumatologia.png"},
                {"Medicina General", "medicina_general.png"},
                {"Neurología", "neurologia.png"},
                {"Oftalmología", "oftalmologia.png"},
        };
        List<Especialidad> list = new ArrayList<>();
        for (String[] row : data) {
            Especialidad e = new Especialidad();
            e.setNombreEspecialidad(row[0]);
            e.setImgEspecialidad(row[1]);
            list.add(e);
        }
        List<Especialidad> saved = especialidadRepo.saveAll(list);
        log.info("[seed] especialidades: {}", saved.size());
        return saved;
    }

    private List<Unidad> seedUnidades() {
        String[][] data = {
                {"Laboratorio Clínico", "lab_clinico.png"},
                {"Imagenología", "imagenologia.png"},
                {"Patología", "patologia.png"},
        };
        List<Unidad> list = new ArrayList<>();
        for (String[] row : data) {
            Unidad u = new Unidad();
            u.setNombreUnidad(row[0]);
            u.setImgUnidad(row[1]);
            list.add(u);
        }
        List<Unidad> saved = unidadRepo.saveAll(list);
        log.info("[seed] unidades: {}", saved.size());
        return saved;
    }

    private List<HorarioTrabajo> seedHorarios() {
        Object[][] data = {
                {DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(14, 0)},
                {DiaSemana.LUNES, LocalTime.of(14, 0), LocalTime.of(20, 0)},
                {DiaSemana.MARTES, LocalTime.of(8, 0), LocalTime.of(14, 0)},
                {DiaSemana.MARTES, LocalTime.of(14, 0), LocalTime.of(20, 0)},
                {DiaSemana.MIERCOLES, LocalTime.of(8, 0), LocalTime.of(14, 0)},
                {DiaSemana.MIERCOLES, LocalTime.of(14, 0), LocalTime.of(20, 0)},
                {DiaSemana.JUEVES, LocalTime.of(8, 0), LocalTime.of(14, 0)},
                {DiaSemana.VIERNES, LocalTime.of(8, 0), LocalTime.of(14, 0)},
                {DiaSemana.VIERNES, LocalTime.of(14, 0), LocalTime.of(20, 0)},
                {DiaSemana.SABADO, LocalTime.of(8, 0), LocalTime.of(13, 0)},
        };
        List<HorarioTrabajo> list = new ArrayList<>();
        for (Object[] row : data) {
            HorarioTrabajo h = new HorarioTrabajo();
            h.setDiaSemana((DiaSemana) row[0]);
            h.setHoraInicio((LocalTime) row[1]);
            h.setHoraFin((LocalTime) row[2]);
            list.add(h);
        }
        List<HorarioTrabajo> saved = horarioTrabajoRepo.saveAll(list);
        log.info("[seed] horarios_trabajo: {}", saved.size());
        return saved;
    }

    private List<Medico> seedMedicos(List<Especialidad> especialidades) {
        // {idUsuario, especialidadIndex(0-based)}
        int[][] data = {
                {2, 0}, {3, 1}, {4, 2}, {5, 3}, {6, 4}, {7, 5}, {8, 6}, {9, 7},
        };
        List<Medico> list = new ArrayList<>();
        for (int[] row : data) {
            Medico m = new Medico();
            m.setIdUsuario(row[0]);
            m.setEspecialidad(especialidades.get(row[1]));
            list.add(m);
        }
        List<Medico> saved = medicoRepo.saveAll(list);
        log.info("[seed] medicos: {}", saved.size());
        return saved;
    }

    private List<Tecnico> seedTecnicos(List<Unidad> unidades) {
        // {idUsuario, unidadIndex(0-based)}
        int[][] data = {
                {13, 0}, {14, 1}, {15, 2},
        };
        List<Tecnico> list = new ArrayList<>();
        for (int[] row : data) {
            Tecnico t = new Tecnico();
            t.setIdUsuario(row[0]);
            t.setUnidad(unidades.get(row[1]));
            list.add(t);
        }
        List<Tecnico> saved = tecnicoRepo.saveAll(list);
        log.info("[seed] tecnicos: {}", saved.size());
        return saved;
    }

    private void seedEnfermeros(List<HorarioTrabajo> horarios) {
        // {idUsuario, horarioIndex(0-based)} -> horario ids 1,4,8
        int[][] data = {
                {10, 0}, {11, 3}, {12, 7},
        };
        List<Enfermero> list = new ArrayList<>();
        for (int[] row : data) {
            Enfermero e = new Enfermero();
            e.setIdUsuario(row[0]);
            e.setHorario(horarios.get(row[1]));
            list.add(e);
        }
        enfermeroRepo.saveAll(list);
        log.info("[seed] enfermeros: {}", list.size());
    }

    private void seedHorarioMedico(List<Medico> medicos, List<HorarioTrabajo> horarios) {
        // {medicoIndex(0-based), horarioIndex(0-based)} -> plan pairs by id
        int[][] pairs = {
                {0, 0}, {0, 2}, {1, 0}, {1, 4}, {2, 1}, {3, 2},
                {3, 6}, {4, 4}, {5, 0}, {5, 7}, {6, 5}, {7, 7},
        };
        for (int[] p : pairs) {
            em.createNativeQuery("INSERT INTO horario_medico (id_medico, id_horario) VALUES (?, ?)")
                    .setParameter(1, medicos.get(p[0]).getIdMedico())
                    .setParameter(2, horarios.get(p[1]).getIdHorario())
                    .executeUpdate();
        }
        log.info("[seed] horario_medico: {}", pairs.length);
    }

    private void seedHorarioTecnico(List<Tecnico> tecnicos, List<HorarioTrabajo> horarios) {
        // {tecnicoIndex(0-based), horarioIndex(0-based)}
        int[][] pairs = {
                {0, 0}, {0, 2}, {1, 4}, {2, 7},
        };
        for (int[] p : pairs) {
            em.createNativeQuery("INSERT INTO horario_tecnico (id_tecnico, id_horario) VALUES (?, ?)")
                    .setParameter(1, tecnicos.get(p[0]).getIdTecnico())
                    .setParameter(2, horarios.get(p[1]).getIdHorario())
                    .executeUpdate();
        }
        log.info("[seed] horario_tecnico: {}", pairs.length);
    }
}
