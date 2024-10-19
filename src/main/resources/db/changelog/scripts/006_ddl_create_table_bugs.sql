CREATE TABLE bugs
(
    bug_id      BIGSERIAL PRIMARY KEY,
    task_id     BIGINT      NOT NULL,
    tester_id   BIGINT      NOT NULL,
    description TEXT        NOT NULL,
    priority    VARCHAR(50) NOT NULL,
    status      VARCHAR(50) NOT NULL,
    CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks (task_id),
    CONSTRAINT fk_tester FOREIGN KEY (tester_id) REFERENCES employees (employee_id)
);
