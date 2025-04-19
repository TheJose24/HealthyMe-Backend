package studio.devbyjose.healthyme_storage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_storage.entity.FileMetadata;

@Mapper(componentModel = "spring")
public abstract class FileMetadataMapper {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${server.port}")
    private String serverPort;

    @Mapping(target = "downloadUrl", source = "filename", qualifiedByName = "createDownloadUrl")
    @Mapping(target = "lastAccessDate", source = "lastAccessed")
    public abstract FileMetadataDTO toDto(FileMetadata fileMetadata);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "storagePath", ignore = true)
    @Mapping(target = "checksum", ignore = true)
    @Mapping(target = "lastAccessed", source = "lastAccessDate")
    public abstract FileMetadata toEntity(FileMetadataDTO fileMetadataDTO);

    @Named("createDownloadUrl")
    protected String createDownloadUrl(String filename) {
        return "http://localhost:" + serverPort + contextPath + "/api/storage/files/" + filename;
    }
}