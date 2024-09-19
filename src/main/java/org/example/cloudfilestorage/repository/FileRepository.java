package org.example.cloudfilestorage.repository;

import org.example.cloudfilestorage.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findByUserId(Long userId);
    List<File> findByFolderId(Integer folderId);
    List<File> findByFilenameContaining(String filename);
    List<File> findByUserIdAndFolderId(Long userId, Integer folderId);
}
