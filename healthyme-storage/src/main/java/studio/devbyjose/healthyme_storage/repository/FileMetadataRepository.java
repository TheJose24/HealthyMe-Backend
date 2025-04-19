package studio.devbyjose.healthyme_storage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import studio.devbyjose.healthyme_storage.entity.FileMetadata;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findByModule(String module);

    Optional<FileMetadata> findByFilename(String filename);

    @Query("SELECT COALESCE(SUM(f.size), 0) FROM FileMetadata f")
    long sumSizes();

}