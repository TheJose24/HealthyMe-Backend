package studio.devbyjose.healthyme_storage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_storage.dto.FileUploadDTO;
import studio.devbyjose.healthyme_commons.client.dto.StorageResponseDTO;
import studio.devbyjose.healthyme_storage.service.interfaces.StorageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Slf4j
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<StorageResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "reference_id", required = false) String referenceId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "is_public", required = false) Boolean isPublic) {

        log.info("Recibido archivo para subir: {}, tamaño: {}, tipo: {}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        // Verificar espacio antes de procesar
        if (!storageService.hasEnoughSpace(file.getSize())) {
            return ResponseEntity
                    .status(HttpStatus.INSUFFICIENT_STORAGE)
                    .body(StorageResponseDTO.builder()
                            .success(false)
                            .message("No hay suficiente espacio disponible para almacenar el archivo")
                            .build());
        }

        StorageResponseDTO response = storageService.store(file, module, referenceId, description, isPublic);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/base64")
    public ResponseEntity<StorageResponseDTO> uploadBase64File(
            @Valid @RequestBody FileUploadDTO fileUploadDTO) {

        log.info("Recibido archivo base64 para subir: {}, tipo: {}",
                fileUploadDTO.getFilename(), fileUploadDTO.getContentType());

        StorageResponseDTO response = storageService.storeBase64(fileUploadDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        byte[] fileContent = storageService.loadAsBytes(filename);
        FileMetadataDTO metadata = storageService.getMetadataByFilename(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metadata.getOriginalFilename() + "\"")
                .body(fileContent);
    }

    @GetMapping("/metadata/{id}")
    public ResponseEntity<FileMetadataDTO> getFileMetadata(@PathVariable Long id) {
        FileMetadataDTO metadata = storageService.getMetadata(id);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/metadata/filename/{filename}")
    public ResponseEntity<FileMetadataDTO> getFileMetadataByFilename(@PathVariable String filename) {
        FileMetadataDTO metadata = storageService.getMetadataByFilename(filename);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/files/module/{module}")
    public ResponseEntity<List<FileMetadataDTO>> getFilesByModule(@PathVariable String module) {
        List<FileMetadataDTO> files = storageService.listByModule(module);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        storageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/files/filename/{filename}")
    public ResponseEntity<Void> deleteFileByFilename(@PathVariable String filename) {
        storageService.deleteByFilename(filename);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/files/increment-access/{id}")
    public ResponseEntity<Void> incrementAccessCount(@PathVariable Long id) {
        storageService.incrementAccessCount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/system/stats")
    public ResponseEntity<Map<String, Object>> getStorageStats() {
        Map<String, Object> stats = storageService.getStorageStats();
        return ResponseEntity.ok(stats);
    }


}