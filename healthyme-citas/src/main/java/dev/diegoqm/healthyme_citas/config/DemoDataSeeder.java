package dev.diegoqm.healthyme_citas.config;

import dev.diegoqm.healthyme_citas.entity.Cita;
import dev.diegoqm.healthyme_citas.enums.EstadoCita;
import dev.diegoqm.healthyme_citas.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final CitaRepository citaRepository;

    @Override
    public void run(String... args) {
        if (citaRepository.count() > 0) {
            log.info("[seed] citas already present, skipping");
            return;
        }

        List<Cita> citas = List.of(
                cita(1, "2026-06-01", "09:00", EstadoCita.REALIZADA, 1L, 1, 1L),
                cita(2, "2026-06-02", "09:30", EstadoCita.REALIZADA, 2L, 2, 3L),
                cita(3, "2026-06-03", "10:00", EstadoCita.REALIZADA, 3L, 1, 1L),
                cita(4, "2026-06-04", "08:45", EstadoCita.REALIZADA, 4L, 4, 6L),
                cita(5, "2026-06-05", "11:00", EstadoCita.REALIZADA, 5L, 6, 2L),
                cita(6, "2026-06-06", "09:15", EstadoCita.REALIZADA, 6L, 2, 3L),
                cita(7, "2026-06-08", "10:30", EstadoCita.REALIZADA, 7L, 1, 10L),
                cita(8, "2026-06-09", "08:30", EstadoCita.REALIZADA, 8L, 7, 4L),
                cita(9, "2026-06-10", "09:45", EstadoCita.REALIZADA, 9L, 6, 9L),
                cita(10, "2026-06-11", "11:15", EstadoCita.REALIZADA, 10L, 6, 2L),
                cita(11, "2026-06-12", "08:00", EstadoCita.REALIZADA, 11L, 1, 1L),
                cita(12, "2026-06-13", "10:00", EstadoCita.REALIZADA, 12L, 3, 5L),
                cita(13, "2026-06-15", "09:00", EstadoCita.REALIZADA, 13L, 6, 9L),
                cita(14, "2026-06-17", "10:45", EstadoCita.REALIZADA, 15L, 5, 8L),
                cita(15, "2026-06-19", "11:30", EstadoCita.REALIZADA, 17L, 6, 2L),
                cita(16, "2026-07-21", "09:00", EstadoCita.PENDIENTE, 1L, 1, 1L),
                cita(17, "2026-07-22", "10:00", EstadoCita.PENDIENTE, 3L, 8, 7L),
                cita(18, "2026-07-22", "11:00", EstadoCita.PENDIENTE, 5L, 5, 8L),
                cita(19, "2026-07-23", "08:30", EstadoCita.PENDIENTE, 7L, 1, 10L),
                cita(20, "2026-07-23", "09:30", EstadoCita.PENDIENTE, 9L, 2, 3L),
                cita(21, "2026-07-24", "10:30", EstadoCita.PENDIENTE, 11L, 4, 6L),
                cita(22, "2026-07-24", "11:30", EstadoCita.PENDIENTE, 13L, 7, 4L),
                cita(23, "2026-07-25", "09:00", EstadoCita.PENDIENTE, 14L, 2, 3L),
                cita(24, "2026-07-25", "10:00", EstadoCita.PENDIENTE, 16L, 6, 9L),
                cita(25, "2026-07-28", "08:45", EstadoCita.PENDIENTE, 18L, 3, 5L),
                cita(26, "2026-07-28", "11:15", EstadoCita.PENDIENTE, 20L, 8, 7L),
                cita(27, "2026-06-05", "14:00", EstadoCita.CANCELADA, 2L, 5, 8L),
                cita(28, "2026-06-10", "15:00", EstadoCita.CANCELADA, 6L, 4, 6L),
                cita(29, "2026-06-14", "09:00", EstadoCita.CANCELADA, 10L, 7, 4L),
                cita(30, "2026-06-18", "16:00", EstadoCita.CANCELADA, 19L, 1, 1L)
        );

        citaRepository.saveAll(citas);
        log.info("[seed] inserted {} citas", citas.size());
    }

    private Cita cita(int n, String fecha, String hora, EstadoCita estado,
                      Long idPaciente, Integer idMedico, Long idConsultorio) {
        return Cita.builder()
                .id(String.format("00000000-0000-0000-0000-%012d", n))
                .fecha(LocalDate.parse(fecha))
                .hora(LocalTime.parse(hora))
                .estado(estado)
                .idPaciente(idPaciente)
                .idMedico(idMedico)
                .idConsultorio(idConsultorio)
                .build();
    }
}
