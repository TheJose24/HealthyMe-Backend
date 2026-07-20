package studio.devbyjose.healthyme_storage.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_storage.entity.FileMetadata;
import studio.devbyjose.healthyme_storage.repository.FileMetadataRepository;

import java.util.List;

@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final FileMetadataRepository fileMetadataRepository;

    @Override
    public void run(String... args) {
        if (fileMetadataRepository.count() > 0) {
            log.info("[seed] file_metadata already present, skipping");
            return;
        }

        // Seed into empty table in canonical order so IDENTITY yields ids 1..10.
        // createdDate/modifiedDate auto-populate via JPA auditing on save; createdBy
        // set explicitly since no request-scoped AuditorAware exists during seeding.
        List<FileMetadata> files = List.of(
                file("3f7a_perfil_juan.jpg", "perfil.jpg", "image/jpeg", 84321L,
                        "/storage/PACIENTES/1/perfil.jpg", "PACIENTES", "1", "jperez", false,
                        "Foto de perfil del paciente"),
                file("a91c_resultado_glucosa.pdf", "resultado_glucosa.pdf", "application/pdf", 245678L,
                        "/storage/LABORATORIO/3/resultado.pdf", "LABORATORIO", "3", "pcardenas", false,
                        "Resultado examen glucosa"),
                file("b2d4_factura_5.pdf", "factura.pdf", "application/pdf", 132045L,
                        "/storage/PAYMENT/5/factura.pdf", "PAYMENT", "5", "admin", false,
                        "Factura F001-00000005"),
                file("c5e8_receta_1.pdf", "receta.pdf", "application/pdf", 98211L,
                        "/storage/CONSULTAS/1/receta.pdf", "CONSULTAS", "1", "jquispe", false,
                        "Receta médica generada"),
                file("d7f1_radiografia_8.dcm", "radiografia.dcm", "application/dicom", 1548900L,
                        "/storage/LABORATORIO/6/radiografia.dcm", "LABORATORIO", "6", "ahuamani", false,
                        "Radiografía de tórax"),
                file("e3a9_comprobante_rec1.pdf", "comprobante_pago.pdf", "application/pdf", 245678L,
                        "/storage/RECLAMACIONES/1/comprobante.pdf", "RECLAMACIONES", "1", "admin", false,
                        "Evidencia de reclamación"),
                file("0b1c_perfil_medico2.jpg", "dr_quispe.jpg", "image/jpeg", 76543L,
                        "/storage/PERSONAL/1/perfil.jpg", "PERSONAL", "1", "jquispe", true,
                        "Foto pública del médico"),
                file("4d6e_biopsia_9.pdf", "biopsia.pdf", "application/pdf", 187233L,
                        "/storage/LABORATORIO/9/biopsia.pdf", "LABORATORIO", "9", "dpalomino", false,
                        "Informe de biopsia"),
                file("8f2b_factura_9.pdf", "factura.pdf", "application/pdf", 130998L,
                        "/storage/PAYMENT/9/factura.pdf", "PAYMENT", "9", "admin", false,
                        "Factura F001-00000009"),
                file("6c9a_logo_clinica.png", "logo.png", "image/png", 45120L,
                        "/storage/SISTEMA/logo.png", "SISTEMA", null, "admin", true,
                        "Logo institucional HealthyMe")
        );

        fileMetadataRepository.saveAll(files);
        log.info("[seed] inserted {} file_metadata rows", files.size());
    }

    private FileMetadata file(String filename, String originalFilename, String contentType, Long size,
                              String storagePath, String module, String referenceId, String createdBy,
                              boolean isPublic, String description) {
        return FileMetadata.builder()
                .filename(filename)
                .originalFilename(originalFilename)
                .contentType(contentType)
                .size(size)
                .storagePath(storagePath)
                .module(module)
                .referenceId(referenceId)
                .createdBy(createdBy)
                .isPublic(isPublic)
                .description(description)
                .build();
    }
}
