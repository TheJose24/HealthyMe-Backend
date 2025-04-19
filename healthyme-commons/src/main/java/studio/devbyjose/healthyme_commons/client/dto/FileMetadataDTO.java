package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataDTO {
    private Long id;
    private String filename;
    private String originalFilename;
    private String contentType;
    private Long size;
    private String module;
    private String referenceId;
    private String description;
    private Boolean isPublic;
    private Integer accessCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastAccessDate;
    private String downloadUrl;
}