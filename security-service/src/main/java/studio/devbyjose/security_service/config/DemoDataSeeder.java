package studio.devbyjose.security_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import studio.devbyjose.security_service.entity.Contrato;
import studio.devbyjose.security_service.entity.Persona;
import studio.devbyjose.security_service.entity.Rol;
import studio.devbyjose.security_service.entity.Usuario;
import studio.devbyjose.security_service.enums.EstadoUsuario;
import studio.devbyjose.security_service.repository.ContratoRepository;
import studio.devbyjose.security_service.repository.PersonaRepository;
import studio.devbyjose.security_service.repository.RolRepository;
import studio.devbyjose.security_service.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Seeds canonical demo data for the security-service (DB healthyme_auth).
 * Active only under the "seed" profile.
 *
 * Insert order (FK-safe): Rol -> Persona -> Usuario -> Contrato.
 * Roles are upserted individually (guarded by findByNombreRol) because script.sql
 * seeds them against a non-existent column and cannot be relied upon.
 * Usuario/Contrato/Persona insertion is guarded by usuarioRepository.count().
 */
@Slf4j
@Component
@Profile("seed")
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Map<String, Rol> roles = seedRoles();
        seedUsuariosAndContratos(roles);
        log.info("[seed] security-service demo data ready");
    }

    // --- 1.1 Rol (upsert each of the 5 canonical roles) ---
    private Map<String, Rol> seedRoles() {
        Map<String, Rol> roles = new HashMap<>();
        roles.put(Rol.ROLE_DEFAULT, upsertRol(Rol.ROLE_DEFAULT, "Rol por defecto del sistema"));
        roles.put(Rol.ROLE_PACIENTE, upsertRol(Rol.ROLE_PACIENTE, "Paciente registrado en la clínica"));
        roles.put(Rol.ROLE_MEDICO, upsertRol(Rol.ROLE_MEDICO, "Personal médico"));
        roles.put(Rol.ROLE_ENFERMERO, upsertRol(Rol.ROLE_ENFERMERO, "Personal de enfermería y soporte clínico"));
        roles.put(Rol.ROLE_ADMIN, upsertRol(Rol.ROLE_ADMIN, "Administrador del sistema"));
        return roles;
    }

    private Rol upsertRol(String nombreRol, String descripcion) {
        return rolRepository.findByNombreRol(nombreRol).orElseGet(() ->
                rolRepository.save(Rol.builder()
                        .nombreRol(nombreRol)
                        .descripcion(descripcion)
                        .esActivo(true)
                        .build()));
    }

    private void seedUsuariosAndContratos(Map<String, Rol> roles) {
        if (usuarioRepository.count() > 0) {
            log.info("[seed] usuarios already present, skipping persona/usuario/contrato seeding");
            return;
        }

        Rol medico = roles.get(Rol.ROLE_MEDICO);
        Rol enfermero = roles.get(Rol.ROLE_ENFERMERO);
        Rol admin = roles.get(Rol.ROLE_ADMIN);
        Rol paciente = roles.get(Rol.ROLE_PACIENTE);

        String pwAdmin = passwordEncoder.encode("Admin123!");
        String pwMedico = passwordEncoder.encode("Medico123!");
        String pwEnfermero = passwordEncoder.encode("Enfermero123!");
        String pwTecnico = passwordEncoder.encode("Tecnico123!");
        String pwPaciente = passwordEncoder.encode("Paciente123!");

        // --- 1.2 Staff (idUsuario 1-15) ---
        staff("09876543", "Carlos", "Mendoza Rojas", "M", "1980-03-12", "Av. República de Panamá 3050, San Isidro", "998877665", "admin@healthyme.pe", "admin", admin, pwAdmin);
        staff("41000001", "Javier", "Quispe Huamán", "M", "1975-06-21", "Calle Los Robles 145, San Isidro", "987110001", "jquispe@healthyme.pe", "jquispe", medico, pwMedico);
        staff("41000002", "María", "Fernández Castro", "F", "1982-11-03", "Av. Larco 780, Miraflores", "987110002", "mfernandez@healthyme.pe", "mfernandez", medico, pwMedico);
        staff("41000003", "Lucía", "Torres Vega", "F", "1985-01-19", "Av. Aramburú 220, San Isidro", "987110003", "ltorres@healthyme.pe", "ltorres", medico, pwMedico);
        staff("41000004", "Carmen", "Ríos Salazar", "F", "1978-09-27", "Calle Schell 320, Miraflores", "987110004", "crios@healthyme.pe", "crios", medico, pwMedico);
        staff("41000005", "Roberto", "Chávez Díaz", "M", "1973-04-08", "Av. San Borja Norte 410, San Borja", "987110005", "rchavez@healthyme.pe", "rchavez", medico, pwMedico);
        staff("41000006", "Andrés", "Flores Paredes", "M", "1988-07-15", "Av. Javier Prado Este 560, San Borja", "987110006", "aflores@healthyme.pe", "aflores", medico, pwMedico);
        staff("41000007", "Miguel", "Ramos León", "M", "1976-12-30", "Calle Berlín 190, Miraflores", "987110007", "mramos@healthyme.pe", "mramos", medico, pwMedico);
        staff("41000008", "Patricia", "Gutiérrez Núñez", "F", "1983-02-24", "Av. Angamos Este 1450, Surquillo", "987110008", "pgutierrez@healthyme.pe", "pgutierrez", medico, pwMedico);
        staff("42000001", "Rosa", "Aguilar Mendoza", "F", "1990-05-11", "Av. Brasil 2100, Jesús María", "987220001", "raguilar@healthyme.pe", "raguilar", enfermero, pwEnfermero);
        staff("42000002", "José Luis", "Vargas Ccopa", "M", "1987-08-19", "Av. La Marina 1500, Pueblo Libre", "987220002", "jvargas@healthyme.pe", "jvargas", enfermero, pwEnfermero);
        staff("42000003", "Elena", "Sánchez Ríos", "F", "1992-10-02", "Av. Salaverry 3200, Magdalena", "987220003", "esanchez@healthyme.pe", "esanchez", enfermero, pwEnfermero);
        // técnicos: no TECNICO role -> seeded as ENFERMERO (documented deviation)
        staff("43000001", "Pedro", "Cárdenas Loayza", "M", "1991-03-05", "Av. Universitaria 800, San Miguel", "987330001", "pcardenas@healthyme.pe", "pcardenas", enfermero, pwTecnico);
        staff("43000002", "Ana", "Huamaní Ticona", "F", "1993-06-14", "Av. Arequipa 2450, Lince", "987330002", "ahuamani@healthyme.pe", "ahuamani", enfermero, pwTecnico);
        staff("43000003", "Diego", "Palomino Ríos", "M", "1989-11-28", "Av. Petit Thouars 4200, Miraflores", "987330003", "dpalomino@healthyme.pe", "dpalomino", enfermero, pwTecnico);

        // --- 1.3 Pacientes (idUsuario 16-35) ---
        paciente("44000001", "Juan", "Pérez Gonzales", "M", "1990-01-15", "Calle Los Pinos 120, Surco", "951000001", "juan.perez@gmail.com", "jperez", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000002", "Rosa", "Ramírez Díaz", "F", "1985-07-22", "Av. Benavides 1800, Miraflores", "951000002", "rosa.ramirez@gmail.com", "rramirez", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000003", "Luis", "García Flores", "M", "1978-03-30", "Jr. Cusco 450, Cercado de Lima", "951000003", "luis.garcia@gmail.com", "lgarcia", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000004", "Ana", "Rodríguez Vega", "F", "1995-11-08", "Av. La Molina 2300, La Molina", "951000004", "ana.rodriguez@gmail.com", "arodriguez", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000005", "Carlos", "Sánchez Rojas", "M", "1982-05-17", "Calle Tarapacá 210, Barranco", "951000005", "carlos.sanchez@gmail.com", "csanchez", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000006", "Lucía", "Castro Mendoza", "F", "2000-09-25", "Av. Colonial 1200, Callao", "951000006", "lucia.castro@gmail.com", "lcastro", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000007", "Jorge", "Torres Paredes", "M", "1970-12-01", "Av. Los Próceres 900, San Juan de Lurigancho", "951000007", "jorge.torres@gmail.com", "jtorres", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000008", "Elena", "Flores Quispe", "F", "1988-02-14", "Calle Bolívar 330, Pueblo Libre", "951000008", "elena.flores@gmail.com", "eflores", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000009", "Miguel", "Díaz Huamán", "M", "1993-06-19", "Av. Túpac Amaru 1500, Comas", "951000009", "miguel.diaz@gmail.com", "mdiaz", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000010", "Sofía", "Vargas León", "F", "1997-04-03", "Av. Universitaria 3400, Los Olivos", "951000010", "sofia.vargas@gmail.com", "svargas", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000011", "Fernando", "Gonzales Ríos", "M", "1975-10-11", "Calle Lima 55, Chorrillos", "951000011", "fernando.gonzales@gmail.com", "fgonzales", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000012", "Patricia", "Mendoza Salas", "F", "1991-08-29", "Av. El Sol 780, Villa El Salvador", "951000012", "patricia.mendoza@gmail.com", "pmendoza", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000013", "Ricardo", "Herrera Campos", "M", "1984-01-07", "Av. Guardia Civil 640, San Borja", "951000013", "ricardo.herrera@gmail.com", "rherrera", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000014", "Gabriela", "Portilla Núñez", "F", "1999-03-16", "Calle Grau 145, Magdalena", "951000014", "gabriela.portilla@gmail.com", "gportilla", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000015", "Manuel", "Rojas Aguilar", "M", "1968-07-04", "Av. Brasil 3100, Breña", "951000015", "manuel.rojas@gmail.com", "mrojas", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000016", "Valeria", "Chávez Espinoza", "F", "2002-05-20", "Av. Javier Prado Oeste 1200, Magdalena", "951000016", "valeria.chavez@gmail.com", "vchavez", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000017", "Daniel", "Salazar Ponce", "M", "1986-09-12", "Calle Real 88, Ate", "951000017", "daniel.salazar@gmail.com", "dsalazar", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000018", "Camila", "Reyes Ordoñez", "F", "1994-11-23", "Av. Los Frutales 500, La Molina", "951000018", "camila.reyes@gmail.com", "creyes", paciente, pwPaciente, EstadoUsuario.ACTIVO);
        paciente("44000019", "Óscar", "Medina Vílchez", "M", "1979-02-09", "Av. Grau 1400, La Victoria", "951000019", "oscar.medina@gmail.com", "omedina", paciente, pwPaciente, EstadoUsuario.SUSPENDIDO);
        paciente("44000020", "Isabel", "Ccahuana Mamani", "F", "1996-06-27", "Av. Tomás Marsano 2600, Surco", "951000020", "isabel.ccahuana@gmail.com", "iccahuana", paciente, pwPaciente, EstadoUsuario.ELIMINADO);

        // --- 1.4 Contrato (staff only, idUsuario 1-15) ---
        contrato("09876543", "Administrador de Sistemas", "2022-01-01", "2027-12-31", "9500.00", 48, "Gestión integral de la plataforma");
        contrato("41000001", "Médico Cardiólogo", "2023-02-01", "2027-01-31", "12000.00", 40, "Consultas de cardiología");
        contrato("41000002", "Médico Pediatra", "2023-03-01", "2027-02-28", "11000.00", 40, "Atención pediátrica");
        contrato("41000003", "Médico Dermatólogo", "2023-04-01", "2027-03-31", "11000.00", 36, "Consultas dermatológicas");
        contrato("41000004", "Médico Ginecólogo", "2022-06-01", "2026-12-31", "11500.00", 40, "Ginecología y obstetricia");
        contrato("41000005", "Médico Traumatólogo", "2023-01-15", "2027-01-14", "11800.00", 40, "Traumatología");
        contrato("41000006", "Médico General", "2024-01-01", "2027-12-31", "9000.00", 40, "Medicina general");
        contrato("41000007", "Médico Neurólogo", "2022-09-01", "2026-08-31", "12500.00", 36, "Neurología");
        contrato("41000008", "Médico Oftalmólogo", "2023-05-01", "2027-04-30", "11200.00", 36, "Oftalmología");
        contrato("42000001", "Enfermera", "2023-02-01", "2027-01-31", "4200.00", 48, "Enfermería general");
        contrato("42000002", "Enfermero", "2023-06-01", "2027-05-31", "4200.00", 48, "Enfermería general");
        contrato("42000003", "Enfermera", "2024-03-01", "2028-02-28", "4000.00", 48, "Enfermería general");
        contrato("43000001", "Técnico de Laboratorio", "2023-04-01", "2027-03-31", "3800.00", 48, "Laboratorio clínico");
        contrato("43000002", "Técnica de Imagenología", "2023-07-01", "2027-06-30", "3900.00", 48, "Imagenología");
        contrato("43000003", "Técnico de Patología", "2024-01-01", "2027-12-31", "3700.00", 48, "Patología");
    }

    private void staff(String dni, String nombre, String apellido, String sexo, String fechaNac,
                       String direccion, String telefono, String email, String nombreUsuario,
                       Rol rol, String encodedPw) {
        persist(dni, nombre, apellido, sexo, fechaNac, direccion, telefono, email,
                nombreUsuario, rol, encodedPw, EstadoUsuario.ACTIVO);
    }

    private void paciente(String dni, String nombre, String apellido, String sexo, String fechaNac,
                          String direccion, String telefono, String email, String nombreUsuario,
                          Rol rol, String encodedPw, EstadoUsuario estado) {
        persist(dni, nombre, apellido, sexo, fechaNac, direccion, telefono, email,
                nombreUsuario, rol, encodedPw, estado);
    }

    private void persist(String dni, String nombre, String apellido, String sexo, String fechaNac,
                         String direccion, String telefono, String email, String nombreUsuario,
                         Rol rol, String encodedPw, EstadoUsuario estado) {
        Persona persona = personaRepository.save(Persona.builder()
                .dni(dni)
                .nombre(nombre)
                .apellido(apellido)
                .sexo(sexo)
                .fechaNacimiento(LocalDate.parse(fechaNac))
                .direccion(direccion)
                .telefono(telefono)
                .email(email)
                .build());

        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .contrasena(encodedPw)
                .estado(estado)
                .rol(rol)
                .persona(persona)
                .intentosFallidos(0)
                .build());
    }

    private void contrato(String dni, String cargo, String inicio, String fin,
                          String salario, int horasSemana, String descripcion) {
        Usuario usuario = usuarioRepository.findByPersonaDni(dni).orElseThrow(() ->
                new IllegalStateException("[seed] usuario for dni " + dni + " not found for contrato"));
        contratoRepository.save(Contrato.builder()
                .usuario(usuario)
                .cargo(cargo)
                .fechaInicio(LocalDate.parse(inicio))
                .fechaFin(LocalDate.parse(fin))
                .salario(new BigDecimal(salario))
                .horasSemana(horasSemana)
                .descripcion(descripcion)
                .build());
    }
}
