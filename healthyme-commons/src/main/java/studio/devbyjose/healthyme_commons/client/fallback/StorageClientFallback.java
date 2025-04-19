package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_commons.client.feign.StorageClient;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StorageClientFallback implements StorageClient {

    @Override
    public ResponseEntity<byte[]> getFile(String filename) {
        log.error("Fallback: No se pudo obtener el archivo {}", filename);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<FileMetadataDTO> getFileMetadata(String filename) {
        log.error("Fallback: No se pudieron obtener los metadatos del archivo {}", filename);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<FileMetadataDTO>> getFilesByModule(String module) {
        log.error("Fallback: No se pudieron obtener los archivos del módulo {}", module);
        return ResponseEntity.ok(Collections.emptyList());
    }
}