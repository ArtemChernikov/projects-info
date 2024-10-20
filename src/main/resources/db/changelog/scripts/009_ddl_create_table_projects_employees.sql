CREATE TABLE projects_employees
(
    id          BIGSERIAL PRIMARY KEY,
    project_id  BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES projects (project_id),
    CONSTRAINT fk_employee_id FOREIGN KEY (employee_id) REFERENCES employees (employee_id)
);

