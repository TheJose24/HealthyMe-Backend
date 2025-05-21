package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_commons.client.dto.StorageResponseDTO;
import studio.devbyjose.healthyme_commons.client.fallback.StorageClientFallback;

import java.util.List;

@FeignClient(name = "healthyme-storage", fallback = StorageClientFallback.class)
public interface StorageClient {

    @PostMapping("/api/storage/upload")
    ResponseEntity<StorageResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "reference_id", required = false) String referenceId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "is_public", required = false) Boolean isPublic);

    @GetMapping("/api/storage/files/{filename}")
    ResponseEntity<byte[]> getFile(@PathVariable("filename") String filename);

    @GetMapping("/api/storage/metadata/filename/{filename}")
    ResponseEntity<FileMetadataDTO> getFileMetadata(@PathVariable("filename") String filename);

    @GetMapping("/api/storage/files/module/{module}")
    ResponseEntity<List<FileMetadataDTO>> getFilesByModule(@PathVariable("module") String module);
}