package org.example.cloudfilestorage.repository;

import org.example.cloudfilestorage.model.foledr.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findByUserId(Long userId);
    List<Folder> findByParentFolderId(Integer parentFolderId);
}
