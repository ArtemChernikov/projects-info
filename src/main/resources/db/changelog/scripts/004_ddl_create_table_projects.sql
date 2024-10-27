CREATE TABLE IF NOT EXISTS projects
(
    project_id BIGSERIAL PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    start_date DATE         NOT NULL,
    end_date   DATE,
    status     VARCHAR(50)  NOT NULL
);
