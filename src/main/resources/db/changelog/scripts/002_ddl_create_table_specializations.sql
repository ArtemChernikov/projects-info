CREATE TABLE IF NOT EXISTS specializations
(
    specialization_id   BIGSERIAL PRIMARY KEY,
    specialization_name VARCHAR(100) NOT NULL UNIQUE
);
