CREATE TABLE IF NOT EXISTS employees
(
    employee_id       BIGSERIAL PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    specialization_id BIGINT       NOT NULL,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    patronymic_name   VARCHAR(100),
    date_of_birth     DATE         NOT NULL,
    phone             VARCHAR(15)  NOT NULL UNIQUE,
    email             VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_specialization FOREIGN KEY (specialization_id) REFERENCES specializations (specialization_id)
);
