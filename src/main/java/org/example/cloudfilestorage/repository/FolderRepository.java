package org.example.cloudfilestorage.repository;

import org.example.cloudfilestorage.model.foledr.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findByUserId(Long userId);
    List<Folder> findByParentFolderId(Integer parentFolderId);
    Optional<Folder> findByName(String folderName);
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND (f.parentFolder.id = :parentFolderId OR (f.parentFolder IS NULL AND :parentFolderId IS NULL))")
    Optional<Folder> findByUserIdAndParentFolderId(@Param("userId") Long userId,
                                                   @Param("parentFolderId") Integer parentFolderId);


    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.id = :id")
    Optional<Folder> findByUserIdAndId(@Param("userId") Long userId, @Param("id") Integer id);

    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.name = :name")
    Optional<Folder> findByUserIdAndName(@Param("userId") Long userId,
                                         @Param("name") String name);

    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.parentFolder.id = :parentFolderId")
    List<Folder> findSubFolders(@Param("userId") Long userId, @Param("parentFolderId") Integer parentFolderId);
}
