CREATE TABLE employees
(
    employee_id       BIGSERIAL PRIMARY KEY,
    role_id           BIGINT       NOT NULL,
    specialization_id BIGINT       NOT NULL,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    patronymic_name   VARCHAR(100),
    date_of_birth     DATE         NOT NULL,
    phone             VARCHAR(15)  NOT NULL UNIQUE,
    email             VARCHAR(100) NOT NULL UNIQUE,
    login             VARCHAR(50)  NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (role_id),
    CONSTRAINT fk_specialization FOREIGN KEY (specialization_id) REFERENCES specializations (specialization_id)
);
