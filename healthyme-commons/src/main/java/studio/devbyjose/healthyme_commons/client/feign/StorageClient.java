package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_commons.client.fallback.StorageClientFallback;

@FeignClient(name = "healthyme-storage", fallback = StorageClientFallback.class)
public interface StorageClient {

    @GetMapping("/api/storage/files/{filename}")
    ResponseEntity<byte[]> getFile(@PathVariable("filename") String filename);

    @GetMapping("/api/storage/metadata/filename/{filename}")
    ResponseEntity<FileMetadataDTO> getFileMetadata(@PathVariable("filename") String filename);

    @GetMapping("/api/storage/files/module/{module}")
    ResponseEntity<java.util.List<FileMetadataDTO>> getFilesByModule(@PathVariable("module") String module);
}