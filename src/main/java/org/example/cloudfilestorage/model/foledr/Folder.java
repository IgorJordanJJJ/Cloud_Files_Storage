package org.example.cloudfilestorage.model.foledr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.example.cloudfilestorage.model.File;
import org.example.cloudfilestorage.model.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    @Pattern(regexp = "^[a-zA-Z0-9-_ ]+$", message = "Название содержит недопустимые символы")
    @NotNull(message = "Название не может быть пустым")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    @JsonIgnore // Предотвращает циклическую зависимость при сериализации в JSON
    private Folder parentFolder;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Тип фала не может быть пустым")
    private EFolder folderType;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore // Предотвращает циклическую зависимость при сериализации в JSON
    private Set<Folder> subFolders;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<File> files;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // !!! это не ссылка которую генерирует Minio это ссылка на контроллер через который можно скачать файл
    @Column(length = 500)
    @NotNull(message = "Ссылка для скачивания не может быть пустой")
    private String downloadUrlApp;


    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", parentFolder=" + parentFolder +
                ", folderType=" + folderType +
                ", subFolders=" + subFolders +
                ", files=" + files +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", downloadUrlApp='" + downloadUrlApp + '\'' +
                '}';
    }

    // Метод для установки URL, если folderType не SYSTEM
    public void updateDownloadUrlApp() {
//        if (this.folderType != EFolder.SYSTEM) {
//            this.downloadUrlApp = "/files/download/folder/" + this.id;
//        } else {
//            this.downloadUrlApp = null; // Или оставить пустым, если это ваш предпочтительный вариант
//        }
        this.downloadUrlApp = "/files/download/folder/" + this.name;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Folder folder = (Folder) o;
        return getId() != null && Objects.equals(getId(), folder.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
