package studio.devbyjose.healthyme_storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDTO {
    @NotBlank(message = "El contenido en base64 es obligatorio")
    private String contentBase64;

    @NotBlank(message = "El nombre del archivo es obligatorio")
    private String filename;

    @NotBlank(message = "El tipo de contenido es obligatorio")
    private String contentType;

    private String module;

    private String referenceId;

    private String description;

    private Boolean isPublic;
}