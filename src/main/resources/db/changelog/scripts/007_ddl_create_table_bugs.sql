CREATE TABLE IF NOT EXISTS bugs
(
    bug_id      BIGSERIAL PRIMARY KEY,
    project_id  BIGINT      NOT NULL,
    name        varchar(50) NOT NULL,
    description TEXT        NOT NULL,
    priority    VARCHAR(50) NOT NULL,
    status      VARCHAR(50) NOT NULL,
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES projects (project_id)
);
