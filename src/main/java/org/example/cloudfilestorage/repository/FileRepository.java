package org.example.cloudfilestorage.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.cloudfilestorage.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUserId(Long userId);

    List<File> findByFolderId(Long folderId);

    List<File> findByFilenameContaining(String filename);

    List<File> findByUserIdAndFolderId(Long userId, Long folderId);

    @Query("SELECT f FROM File f WHERE f.id = :id AND f.user.id = :userId")
    Optional<File> findByUserIdAndId(@Param("id") Long id, @Param("userId") Long userId);
}
