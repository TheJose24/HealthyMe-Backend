package studio.devbyjose.healthyme_storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageResponseDTO {
    private Long fileId;
    private String filename;
    private String downloadUrl;
    private String message;
    private boolean success;
}