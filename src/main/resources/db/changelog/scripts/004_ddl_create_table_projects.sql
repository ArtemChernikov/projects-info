CREATE TABLE projects
(
    project_id BIGSERIAL PRIMARY KEY,
    manager_id BIGINT       NOT NULL,
    name       VARCHAR(200) NOT NULL,
    start_date DATE         NOT NULL,
    end_date   DATE,
    status     VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_manager FOREIGN KEY (manager_id) REFERENCES employees (employee_id)
);
