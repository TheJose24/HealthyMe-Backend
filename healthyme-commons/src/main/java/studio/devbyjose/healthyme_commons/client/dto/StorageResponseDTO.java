package studio.devbyjose.healthyme_commons.client.dto;

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
    private String contentType;
    private String message;
    private boolean success;
}