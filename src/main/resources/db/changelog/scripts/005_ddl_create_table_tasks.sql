CREATE TABLE tasks
(
    task_id      BIGSERIAL PRIMARY KEY,
    project_id   BIGINT       NOT NULL,
    developer_id BIGINT       NOT NULL,
    name         VARCHAR(200) NOT NULL,
    description  TEXT,
    task_type    VARCHAR(50)  NOT NULL,
    priority     VARCHAR(50)  NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES projects (project_id),
    CONSTRAINT fk_developer FOREIGN KEY (developer_id) REFERENCES employees (employee_id)
);