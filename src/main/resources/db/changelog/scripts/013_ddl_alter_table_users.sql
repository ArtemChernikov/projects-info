-- Добавляем поле photo_id в таблицу users
ALTER TABLE users
    ADD COLUMN photo_id BIGINT;

-- Устанавливаем внешний ключ для поля photo_id, ссылающийся на photos.photo_id
ALTER TABLE users
    ADD CONSTRAINT fk_photo FOREIGN KEY (photo_id) REFERENCES photos (photo_id)
        ON DELETE SET NULL;