package org.example.cloudfilestorage.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.cloudfilestorage.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findByUserId(Long userId);

    List<File> findByFolderId(Integer folderId);

    List<File> findByFilenameContaining(String filename);

    List<File> findByUserIdAndFolderId(Long userId, Integer folderId);

    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.id = :id")
    Optional<File> findByUserAndId(@Param("userId") Long id, @Param("id") Long fileId);
}
