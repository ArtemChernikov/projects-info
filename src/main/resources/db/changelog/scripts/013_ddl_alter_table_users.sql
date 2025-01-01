ALTER TABLE users
    ADD COLUMN photo_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_photo FOREIGN KEY (photo_id) REFERENCES photos (photo_id)
        ON DELETE SET NULL;