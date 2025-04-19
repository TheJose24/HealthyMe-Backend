package studio.devbyjose.healthyme_storage.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;
import studio.devbyjose.healthyme_storage.dto.FileUploadDTO;
import studio.devbyjose.healthyme_storage.dto.StorageResponseDTO;
import studio.devbyjose.healthyme_storage.entity.FileMetadata;
import studio.devbyjose.healthyme_storage.exception.StorageException;
import studio.devbyjose.healthyme_storage.mapper.FileMetadataMapper;
import studio.devbyjose.healthyme_storage.repository.FileMetadataRepository;
import studio.devbyjose.healthyme_storage.service.interfaces.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.FileStore;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileMetadataMapper fileMetadataMapper;

    private static final String FILE_CB = "fileOperationBreaker";
    private static final String DB_CB = "dbOperationBreaker";

    @Value("${storage.location}")
    private String storageLocation;

    @Value("#{'${storage.allowed-content-types}'.split(',')}")
    private List<String> allowedContentTypes;

    @Value("${storage.max-capacity-mb}")
    private long maxCapacityMb;

    @Override
    @Transactional
    @CircuitBreaker(name = FILE_CB, fallbackMethod = "storeFallback")
    public StorageResponseDTO store(MultipartFile file, String module, String referenceId, String description, Boolean isPublic) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("El archivo está vacío", HttpStatus.BAD_REQUEST);
            }

            if (!isContentTypeAllowed(file.getContentType())) {
                throw new StorageException("Tipo de contenido no permitido: " + file.getContentType(), HttpStatus.NOT_ACCEPTABLE);
            }

            // Verificar espacio disponible
            if (!hasEnoughSpace(file.getSize())) {
                throw new StorageException("No hay suficiente espacio para almacenar el archivo", HttpStatus.INSUFFICIENT_STORAGE);
            }

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String newFilename = UUID.randomUUID() + "." + extension;

            // Crear directorio si no existe
            Path rootLocation = Paths.get(storageLocation);
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // Guardar archivo
            Path destinationFile = rootLocation.resolve(newFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Calcular checksum
            String checksum = calculateChecksum(file.getBytes());

            // Guardar metadatos
            FileMetadata fileMetadata = FileMetadata.builder()
                    .filename(newFilename)
                    .originalFilename(originalFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .storagePath(destinationFile.toString())
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .checksum(checksum)
                    .module(module)
                    .referenceId(referenceId)
                    .description(description)
                    .isPublic(isPublic != null ? isPublic : false)
                    .accessCount(0L)
                    .build();

            FileMetadata saved = fileMetadataRepository.save(fileMetadata);

            return StorageResponseDTO.builder()
                    .fileId(saved.getId())
                    .filename(saved.getFilename())
                    .downloadUrl("/api/storage/files/" + saved.getFilename())
                    .message("Archivo guardado correctamente")
                    .success(true)
                    .build();
        } catch (IOException e) {
            throw new StorageException("Error al guardar el archivo", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    @CircuitBreaker(name = FILE_CB, fallbackMethod = "storeBase64Fallback")
    public StorageResponseDTO storeBase64(FileUploadDTO fileUploadDTO) {
        try {
            if (fileUploadDTO.getContentBase64() == null || fileUploadDTO.getContentBase64().isEmpty()) {
                throw new StorageException("El contenido base64 está vacío", HttpStatus.BAD_REQUEST);
            }

            if (!isContentTypeAllowed(fileUploadDTO.getContentType())) {
                throw new StorageException("Tipo de contenido no permitido: " + fileUploadDTO.getContentType(), HttpStatus.NOT_ACCEPTABLE);
            }

            // Decodificar base64
            byte[] fileContent = Base64.getDecoder().decode(fileUploadDTO.getContentBase64());

            // Verificar espacio disponible
            if (!hasEnoughSpace(fileContent.length)) {
                throw new StorageException("No hay suficiente espacio para almacenar el archivo", HttpStatus.INSUFFICIENT_STORAGE);
            }

            // Generar nombre único para el archivo
            String originalFilename = fileUploadDTO.getFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;

            // Crear directorio si no existe
            Path rootLocation = Paths.get(storageLocation);
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // Guardar archivo
            Path destinationFile = rootLocation.resolve(newFilename);
            Files.write(destinationFile, fileContent);

            // Calcular checksum
            String checksum = calculateChecksum(fileContent);

            // Guardar metadatos
            FileMetadata fileMetadata = FileMetadata.builder()
                    .filename(newFilename)
                    .originalFilename(originalFilename)
                    .contentType(fileUploadDTO.getContentType())
                    .size((long) fileContent.length)
                    .storagePath(destinationFile.toString())
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .checksum(checksum)
                    .module(fileUploadDTO.getModule())
                    .referenceId(fileUploadDTO.getReferenceId())
                    .description(fileUploadDTO.getDescription())
                    .isPublic(fileUploadDTO.getIsPublic() != null ? fileUploadDTO.getIsPublic() : false)
                    .accessCount(0L)
                    .build();

            FileMetadata saved = fileMetadataRepository.save(fileMetadata);

            return StorageResponseDTO.builder()
                    .fileId(saved.getId())
                    .filename(saved.getFilename())
                    .downloadUrl("/api/storage/files/" + saved.getFilename())
                    .message("Archivo guardado correctamente")
                    .success(true)
                    .build();
        } catch (IOException e) {
            throw new StorageException("Error al guardar el archivo", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @CircuitBreaker(name = FILE_CB, fallbackMethod = "loadAsBytesFallback")
    public byte[] loadAsBytes(String filename) {
        try {
            FileMetadata metadata = fileMetadataRepository.findByFilename(filename)
                    .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado: " + filename, HttpStatus.NOT_FOUND));

            Path file = Paths.get(metadata.getStoragePath());
            if (!Files.exists(file)) {
                throw new ResourceNotFoundException("Archivo físico no encontrado: " + filename, HttpStatus.NOT_FOUND);
            }

            // Incrementar contador de accesos
            metadata.setAccessCount(metadata.getAccessCount() + 1);
            metadata.setLastAccessed(LocalDateTime.now());
            fileMetadataRepository.save(metadata);

            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new StorageException("Error al leer el archivo: " + filename, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public InputStream loadAsStream(String filename) {
        try {
            FileMetadata metadata = fileMetadataRepository.findByFilename(filename)
                    .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado: " + filename, HttpStatus.NOT_FOUND));

            Path file = Paths.get(metadata.getStoragePath());
            if (!Files.exists(file)) {
                throw new ResourceNotFoundException("Archivo físico no encontrado: " + filename, HttpStatus.NOT_FOUND);
            }

            // Incrementar contador de accesos
            metadata.setAccessCount(metadata.getAccessCount() + 1);
            metadata.setLastAccessed(LocalDateTime.now());
            fileMetadataRepository.save(metadata);

            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new StorageException("Error al leer el archivo: " + filename, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @CircuitBreaker(name = DB_CB, fallbackMethod = "getMetadataFallback")
    public FileMetadataDTO getMetadata(Long id) {
        FileMetadata metadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado con ID: " + id, HttpStatus.NOT_FOUND));
        return fileMetadataMapper.toDto(metadata);
    }

    @Override
    @CircuitBreaker(name = DB_CB, fallbackMethod = "getMetadataByFilenameFallback")
    public FileMetadataDTO getMetadataByFilename(String filename) {
        FileMetadata metadata = fileMetadataRepository.findByFilename(filename)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado: " + filename, HttpStatus.NOT_FOUND));
        return fileMetadataMapper.toDto(metadata);
    }

    @Override
    @CircuitBreaker(name = DB_CB, fallbackMethod = "listByModuleFallback")
    public List<FileMetadataDTO> listByModule(String module) {
        List<FileMetadata> files = fileMetadataRepository.findByModule(module);
        return files.stream()
                .map(fileMetadataMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FileMetadata metadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado con ID: " + id, HttpStatus.NOT_FOUND));

        try {
            // Eliminar archivo físico
            Path file = Paths.get(metadata.getStoragePath());
            if (Files.exists(file)) {
                Files.delete(file);
            }

            // Eliminar metadata
            fileMetadataRepository.delete(metadata);

        } catch (IOException e) {
            throw new StorageException("Error al eliminar el archivo", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteByFilename(String filename) {
        FileMetadata metadata = fileMetadataRepository.findByFilename(filename)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado: " + filename, HttpStatus.NOT_FOUND));

        try {
            // Eliminar archivo físico
            Path file = Paths.get(metadata.getStoragePath());
            if (Files.exists(file)) {
                Files.delete(file);
            }

            // Eliminar metadata
            fileMetadataRepository.delete(metadata);

        } catch (IOException e) {
            throw new StorageException("Error al eliminar el archivo", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void incrementAccessCount(Long id) {
        FileMetadata metadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado con ID: " + id, HttpStatus.NOT_FOUND));

        metadata.setAccessCount(metadata.getAccessCount() + 1);
        metadata.setLastAccessed(LocalDateTime.now());
        fileMetadataRepository.save(metadata);
    }

    @Override
    @CircuitBreaker(name = FILE_CB, fallbackMethod = "getStorageStatsFallback")
    public Map<String, Object> getStorageStats() {
        try {
            Path storagePath = Paths.get(storageLocation);
            Map<String, Object> stats = new HashMap<>();

            // Asegurar que el directorio existe
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }

            // Calcular estadísticas de archivos
            long fileCount = fileMetadataRepository.count();
            long totalSize = fileMetadataRepository.sumSizes();

            // Obtener estadísticas del sistema de archivos
            FileStore store = Files.getFileStore(storagePath);
            long totalSpace = store.getTotalSpace();
            long usableSpace = store.getUsableSpace();
            long usedSpace = totalSpace - usableSpace;

            // Configurar límite (obtener de la propiedad o usar totalSpace)
            long maxCapacityBytes = maxCapacityMb * 1024 * 1024; // Convertir MB a bytes

            // Si el límite configurado es mayor que el espacio total, usar el espacio total
            if (maxCapacityBytes > totalSpace || maxCapacityBytes <= 0) {
                maxCapacityBytes = totalSpace;
            }

            stats.put("fileCount", fileCount);
            stats.put("totalStoredBytes", totalSize);
            stats.put("totalStoredMb", totalSize / (1024 * 1024));
            stats.put("fsCapacityBytes", totalSpace);
            stats.put("fsCapacityMb", totalSpace / (1024 * 1024));
            stats.put("fsUsedBytes", usedSpace);
            stats.put("fsUsedMb", usedSpace / (1024 * 1024));
            stats.put("fsAvailableBytes", usableSpace);
            stats.put("fsAvailableMb", usableSpace / (1024 * 1024));
            stats.put("configuredMaxCapacityMb", maxCapacityMb);
            stats.put("configuredMaxCapacityBytes", maxCapacityBytes);
            stats.put("usagePercentage", (double) usedSpace / totalSpace * 100);

            return stats;
        } catch (IOException e) {
            log.error("Error al obtener estadísticas de almacenamiento", e);
            throw new StorageException("Error al obtener estadísticas de almacenamiento", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean hasEnoughSpace(long fileSize) {
        try {
            Path storagePath = Paths.get(storageLocation);

            // Asegurar que el directorio existe
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }

            // Obtener espacio disponible
            FileStore store = Files.getFileStore(storagePath);
            long usableSpace = store.getUsableSpace();

            // Calcular espacio usado por archivos en la aplicación
            long totalStoredSize = fileMetadataRepository.sumSizes();

            // Calcular límite configurado
            long maxCapacityBytes = maxCapacityMb * 1024 * 1024; // Convertir MB a bytes

            // Si el límite configurado es mayor que el espacio total, usar el espacio total
            if (maxCapacityBytes > store.getTotalSpace() || maxCapacityBytes <= 0) {
                maxCapacityBytes = store.getTotalSpace();
            }

            // Verificar si excede el límite configurado
            if (totalStoredSize + fileSize > maxCapacityBytes) {
                return false;
            }

            // Verificar si hay suficiente espacio en el sistema de archivos
            return usableSpace >= fileSize;

        } catch (IOException e) {
            log.error("Error al verificar espacio disponible", e);
            throw new StorageException("Error al verificar espacio disponible", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public StorageResponseDTO storeFallback(MultipartFile file, String module, String referenceId,
                                            String description, Boolean isPublic, Exception ex) {
        log.error("Circuit breaker activado para store: {}", ex.getMessage());
        return StorageResponseDTO.builder()
                .success(false)
                .message("No se pudo almacenar el archivo debido a un problema técnico. Por favor, inténtelo más tarde.")
                .build();
    }

    public StorageResponseDTO storeBase64Fallback(FileUploadDTO fileUploadDTO, Exception ex) {
        log.error("Circuit breaker activado para storeBase64: {}", ex.getMessage());
        return StorageResponseDTO.builder()
                .success(false)
                .message("No se pudo almacenar el archivo Base64 debido a un problema técnico. Por favor, inténtelo más tarde.")
                .build();
    }

    public byte[] loadAsBytesFallback(String filename, Exception ex) {
        log.error("Circuit breaker activado para loadAsBytes: {}", ex.getMessage());
        // Devolver un array vacío o una imagen predeterminada pequeña
        return new byte[0];
    }

    public FileMetadataDTO getMetadataFallback(Long id, Exception ex) {
        log.error("Circuit breaker activado para getMetadata: {}", ex.getMessage());
        return FileMetadataDTO.builder()
                .id(id)
                .filename("no-disponible")
                .originalFilename("Archivo temporalmente no disponible")
                .contentType("application/octet-stream")
                .description("El sistema no puede recuperar los metadatos en este momento")
                .build();
    }

    public FileMetadataDTO getMetadataByFilenameFallback(String filename, Exception ex) {
        log.error("Circuit breaker activado para getMetadataByFilename: {}", ex.getMessage());
        return FileMetadataDTO.builder()
                .filename(filename)
                .originalFilename("Archivo temporalmente no disponible")
                .contentType("application/octet-stream")
                .description("El sistema no puede recuperar los metadatos en este momento")
                .build();
    }

    public List<FileMetadataDTO> listByModuleFallback(String module, Exception ex) {
        log.error("Circuit breaker activado para listByModule: {}", ex.getMessage());
        return List.of(FileMetadataDTO.builder()
                .filename("no-disponible")
                .originalFilename("Listado temporalmente no disponible")
                .description("El sistema no puede recuperar la lista de archivos en este momento")
                .module(module)
                .build());
    }

    public Map<String, Object> getStorageStatsFallback(Exception ex) {
        log.error("Circuit breaker activado para getStorageStats: {}", ex.getMessage());
        Map<String, Object> fallbackStats = new HashMap<>();
        fallbackStats.put("fileCount", -1);
        fallbackStats.put("status", "DEGRADED");
        fallbackStats.put("message", "Estadísticas temporalmente no disponibles");
        return fallbackStats;
    }

    private boolean isContentTypeAllowed(String contentType) {
        return allowedContentTypes.contains(contentType);
    }

    private String calculateChecksum(byte[] fileContent) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(fileContent);

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.warn("No se pudo calcular el checksum", e);
            return null;
        }
    }
}