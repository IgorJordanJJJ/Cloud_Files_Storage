package org.example.cloudfilestorage.repository;

import org.example.cloudfilestorage.model.foledr.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserId(Long userId);

    List<Folder> findByParentFolderId(Long parentFolderId);

    Optional<Folder> findByName(String folderName);

    @Query("SELECT f FROM Folder f WHERE f.id = :id AND f.user.id = :userId ")
    Optional<Folder> findByIdAndUserId(@Param("id") Long id,
                                         @Param("userId") Long userId);

    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND (f.parentFolder.id = :parentFolderId OR (f.parentFolder IS NULL AND :parentFolderId IS NULL))")
    Optional<Folder> findByUserIdAndParentFolderId(@Param("userId") Long userId,
                                                   @Param("parentFolderId") Integer parentFolderId);


    @Query("SELECT f FROM Folder f WHERE f.id = :id AND f.user.id = :userId")
    Optional<Folder> findByUserIdAndId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.folderPath = :folderPath")
    Optional<Folder> findByUserIdAndName(@Param("userId") Long userId,
                                         @Param("folderPath") String folderPath);

    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.parentFolder.id = :parentFolderId")
    List<Folder> findSubFolders(@Param("userId") Long userId, @Param("parentFolderId") Long parentFolderId);
}
