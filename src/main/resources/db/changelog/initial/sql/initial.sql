CREATE TABLE "user" (
                        id BIGSERIAL PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        enabled BOOLEAN NOT NULL DEFAULT TRUE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE "user" IS 'Таблица для хранения данных пользователя';
COMMENT ON COLUMN "user".id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN "user".username IS 'Имя пользователя (логин), уникальное';
COMMENT ON COLUMN "user".password IS 'Пароль пользователя в зашифрованном виде';
COMMENT ON COLUMN "user".email IS 'Email пользователя, уникальный';
COMMENT ON COLUMN "user".enabled IS 'Флаг активности пользователя, TRUE - активен, FALSE - деактивирован';
COMMENT ON COLUMN "user".created_at IS 'Время создания записи пользователя';
COMMENT ON COLUMN "user".updated_at IS 'Время последнего обновления записи пользователя';


CREATE TABLE role (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(50) NOT NULL UNIQUE,
                      role_description VARCHAR(255)
);

COMMENT ON TABLE role IS 'Таблица для хранения роли пользователя';
COMMENT ON COLUMN role.id IS 'Уникальный идентификатор роли';
COMMENT ON COLUMN role.name IS 'Название роли, уникальное (например, ROLE_USER, ROLE_ADMIN)';
COMMENT ON COLUMN role.role_description IS 'Описание роли';


CREATE TABLE user_role (
                           user_id BIGINT NOT NULL,
                           role_id INTEGER NOT NULL,
                           PRIMARY KEY (user_id, role_id),
                           FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
                           FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

COMMENT ON TABLE user_role IS 'Таблица для связывания пользователя с его ролями (многие ко многим)';
COMMENT ON COLUMN user_role.user_id IS 'Идентификатор пользователя из таблицы "user"';
COMMENT ON COLUMN user_role.role_id IS 'Идентификатор роли из таблицы role';


CREATE TABLE folder (
                        id SERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        parent_folder_id INTEGER,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        folder_type VARCHAR(50) NOT NULL,
                        download_url_app VARCHAR(50) NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
                        FOREIGN KEY (parent_folder_id) REFERENCES folder(id) ON DELETE CASCADE
);

COMMENT ON TABLE folder IS 'Таблица для хранения папки пользователя';
COMMENT ON COLUMN folder.id IS 'Уникальный идентификатор папки';
COMMENT ON COLUMN folder.user_id IS 'Идентификатор пользователя, владельца папки';
COMMENT ON COLUMN folder.name IS 'Название папки';
COMMENT ON COLUMN folder.parent_folder_id IS 'Идентификатор родительской папки (NULL для корневой папки)';
COMMENT ON COLUMN folder.folder_type IS 'Идентификатор родительской папки (NULL для корневой папки)';
COMMENT ON COLUMN folder.created_at IS 'Время создания папки';
COMMENT ON COLUMN folder.updated_at IS 'Время последнего обновления папки';
COMMENT ON COLUMN folder.download_url_app IS 'Ссылка для скачивания';


CREATE TABLE file (
                      id SERIAL PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      folder_id INTEGER,
                      filename VARCHAR(255) NOT NULL,
                      filepath VARCHAR(500) NOT NULL,
                      size BIGINT NOT NULL,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
                      FOREIGN KEY (folder_id) REFERENCES folder(id) ON DELETE SET NULL
);

COMMENT ON TABLE file IS 'Таблица для хранения метаданных файла';
COMMENT ON COLUMN file.id IS 'Уникальный идентификатор файла';
COMMENT ON COLUMN file.user_id IS 'Идентификатор пользователя, владельца файла';
COMMENT ON COLUMN file.folder_id IS 'Идентификатор папки, в которой находится файл (NULL для файлов в корне)';
COMMENT ON COLUMN file.filename IS 'Название файла';
COMMENT ON COLUMN file.filepath IS 'Путь к файлу в хранилище S3';
COMMENT ON COLUMN file.size IS 'Размер файла в байтах';
COMMENT ON COLUMN file.created_at IS 'Время создания файла';
COMMENT ON COLUMN file.updated_at IS 'Время последнего обновления файла';


CREATE INDEX idx_user_username ON "user"(username);
CREATE INDEX idx_file_filename ON file(filename);
CREATE INDEX idx_folder_name ON folder(name);

COMMENT ON INDEX idx_user_username IS 'Индекс для ускорения поиска по имени пользователя';
COMMENT ON INDEX idx_file_filename IS 'Индекс для ускорения поиска по имени файла';
COMMENT ON INDEX idx_folder_name IS 'Индекс для ускорения поиска по названию папки';
