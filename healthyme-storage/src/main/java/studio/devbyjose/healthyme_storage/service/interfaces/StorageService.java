package studio.devbyjose.healthyme_storage.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_storage.dto.FileUploadDTO;
import studio.devbyjose.healthyme_commons.client.dto.StorageResponseDTO;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface StorageService {

    /**
     * Guarda un archivo binario
     */
    StorageResponseDTO store(MultipartFile file, String module, String referenceId, String description, Boolean isPublic);

    /**
     * Guarda un archivo desde un DTO con contenido en base64
     */
    StorageResponseDTO storeBase64(FileUploadDTO fileUploadDTO);

    /**
     * Carga un archivo como Resource
     */
    byte[] loadAsBytes(String filename);

    /**
     * Carga un archivo como InputStream
     */
    InputStream loadAsStream(String filename);

    /**
     * Obtiene metadatos de un archivo
     */
    FileMetadataDTO getMetadata(Long id);

    /**
     * Obtiene metadatos de un archivo por nombre
     */
    FileMetadataDTO getMetadataByFilename(String filename);

    /**
     * Lista todos los archivos de un módulo específico
     */
    List<FileMetadataDTO> listByModule(String module);

    /**
     * Elimina un archivo
     */
    void delete(Long id);

    /**
     * Elimina un archivo por nombre
     */
    void deleteByFilename(String filename);

    /**
     * Incrementa el contador de acceso
     */
    void incrementAccessCount(Long id);

    /**
     * Obtiene estadísticas del almacenamiento
     * @return Mapa con estadísticas como espacio total, espacio usado, espacio disponible, etc.
     */
    Map<String, Object> getStorageStats();

    /**
     * Verifica si hay suficiente espacio para almacenar un archivo
     * @param fileSize Tamaño del archivo en bytes
     * @return true si hay suficiente espacio, false en caso contrario
     */
    boolean hasEnoughSpace(long fileSize);


}