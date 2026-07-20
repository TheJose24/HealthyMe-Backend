package dev.diegoqm.healthyme_infraestructura.config;

import dev.diegoqm.healthyme_infraestructura.entity.Consultorio;
import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import dev.diegoqm.healthyme_infraestructura.entity.Laboratorio;
import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import dev.diegoqm.healthyme_infraestructura.enums.DiaSemana;
import dev.diegoqm.healthyme_infraestructura.repository.ConsultorioRepository;
import dev.diegoqm.healthyme_infraestructura.repository.HorarioTrabajoRepository;
import dev.diegoqm.healthyme_infraestructura.repository.LaboratorioRepository;
import dev.diegoqm.healthyme_infraestructura.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Demo data seeder for healthyme-infraestructura.
 * Insert order (FK deps): HorarioTrabajo -> Sede -> Consultorio & Laboratorio.
 * IDENTITY ids auto-generate: seed empty tables in this exact order to get canonical ids.
 * created_at is auto-filled by Spring JPA auditing (@EnableJpaAuditing is active on the app).
 * Activate with the "seed" Spring profile.
 */
@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final HorarioTrabajoRepository horarioTrabajoRepository;
    private final SedeRepository sedeRepository;
    private final ConsultorioRepository consultorioRepository;
    private final LaboratorioRepository laboratorioRepository;

    @Override
    public void run(String... args) {
        List<HorarioTrabajo> horarios = seedHorarios();
        List<Sede> sedes = seedSedes(horarios);
        seedConsultorios(sedes);
        seedLaboratorios(sedes);
        log.info("[seed] infraestructura demo data seeding complete");
    }

    private List<HorarioTrabajo> seedHorarios() {
        if (horarioTrabajoRepository.count() > 0) {
            log.info("[seed] horario_trabajo already present, skipping");
            return horarioTrabajoRepository.findAll();
        }
        List<HorarioTrabajo> horarios = List.of(
                horario(DiaSemana.LUNES, "08:00", "20:00"),
                horario(DiaSemana.MARTES, "08:00", "20:00"),
                horario(DiaSemana.MIERCOLES, "08:00", "20:00"),
                horario(DiaSemana.JUEVES, "08:00", "20:00"),
                horario(DiaSemana.VIERNES, "08:00", "20:00"),
                horario(DiaSemana.SABADO, "08:00", "13:00")
        );
        List<HorarioTrabajo> saved = horarioTrabajoRepository.saveAll(horarios);
        log.info("[seed] inserted {} horario_trabajo rows", saved.size());
        return saved;
    }

    private List<Sede> seedSedes(List<HorarioTrabajo> horarios) {
        if (sedeRepository.count() > 0) {
            log.info("[seed] sede already present, skipping");
            return sedeRepository.findAll();
        }
        List<Sede> sedes = List.of(
                sede("Sede San Isidro", "Av. República de Panamá 3050, San Isidro", "013456789", "sanisidro@healthyme.pe", horarios.get(0)),
                sede("Sede Miraflores", "Av. Larco 780, Miraflores", "014567890", "miraflores@healthyme.pe", horarios.get(1)),
                sede("Sede San Borja", "Av. San Borja Norte 410, San Borja", "015678901", "sanborja@healthyme.pe", horarios.get(2))
        );
        List<Sede> saved = sedeRepository.saveAll(sedes);
        log.info("[seed] inserted {} sede rows", saved.size());
        return saved;
    }

    private void seedConsultorios(List<Sede> sedes) {
        if (consultorioRepository.count() > 0) {
            log.info("[seed] consultorio already present, skipping");
            return;
        }
        Sede si = sedes.get(0), mf = sedes.get(1), sb = sedes.get(2);
        List<Consultorio> consultorios = List.of(
                consultorio("Consultorio Cardiología SI", 2, 201, 1, si),
                consultorio("Consultorio Medicina General SI", 1, 101, 6, si),
                consultorio("Consultorio Pediatría SI", 3, 301, 2, si),
                consultorio("Consultorio Neurología SI", 3, 305, 7, si),
                consultorio("Consultorio Dermatología MF", 2, 210, 3, mf),
                consultorio("Consultorio Ginecología MF", 2, 215, 4, mf),
                consultorio("Consultorio Oftalmología MF", 1, 110, 8, mf),
                consultorio("Consultorio Traumatología SB", 1, 105, 5, sb),
                consultorio("Consultorio Medicina General SB", 1, 108, 6, sb),
                consultorio("Consultorio Cardiología SB", 2, 205, 1, sb)
        );
        log.info("[seed] inserted {} consultorio rows", consultorioRepository.saveAll(consultorios).size());
    }

    private void seedLaboratorios(List<Sede> sedes) {
        if (laboratorioRepository.count() > 0) {
            log.info("[seed] laboratorio already present, skipping");
            return;
        }
        Sede si = sedes.get(0), mf = sedes.get(1), sb = sedes.get(2);
        List<Laboratorio> laboratorios = List.of(
                laboratorio("Laboratorio Clínico Central", -1, "S01", 1, si),
                laboratorio("Imagenología San Isidro", -1, "S05", 2, si),
                laboratorio("Laboratorio Clínico Miraflores", 1, "115", 1, mf),
                laboratorio("Patología San Borja", -1, "S02", 3, sb)
        );
        log.info("[seed] inserted {} laboratorio rows", laboratorioRepository.saveAll(laboratorios).size());
    }

    private HorarioTrabajo horario(DiaSemana dia, String inicio, String fin) {
        HorarioTrabajo h = new HorarioTrabajo();
        h.setDiaSemana(dia);
        h.setHoraInicio(LocalTime.parse(inicio));
        h.setHoraFin(LocalTime.parse(fin));
        return h;
    }

    private Sede sede(String nombre, String direccion, String telefono, String email, HorarioTrabajo horario) {
        Sede s = new Sede();
        s.setNombre(nombre);
        s.setDireccion(direccion);
        s.setTelefono(telefono);
        s.setEmail(email);
        s.setHorarioTrabajo(horario);
        return s;
    }

    private Consultorio consultorio(String nombre, int piso, int numeroHabitacion, int idEspecialidad, Sede sede) {
        Consultorio c = new Consultorio();
        c.setNombre(nombre);
        c.setPiso(piso);
        c.setNumeroHabitacion(numeroHabitacion);
        c.setIdEspecialidad(idEspecialidad);
        c.setSede(sede);
        return c;
    }

    private Laboratorio laboratorio(String nombre, int piso, String numeroHabitacion, int idUnidad, Sede sede) {
        Laboratorio l = new Laboratorio();
        l.setNombre(nombre);
        l.setPiso(piso);
        l.setNumeroHabitacion(Integer.parseInt(numeroHabitacion.replaceAll("\\D", "")));
        l.setIdUnidad(idUnidad);
        l.setSede(sede);
        return l;
    }
}
