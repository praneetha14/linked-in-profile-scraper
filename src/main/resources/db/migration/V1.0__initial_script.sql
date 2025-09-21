CREATE TABLE IF NOT EXISTS phantom_agent_task
(
    id                  UUID PRIMARY KEY,
    container_id        varchar(100),
    university         varchar(255),
    current_designation varchar(150),
    passed_out_year     int,
    task_status         varchar(50),
    retry_count         int default 0,
    UNIQUE (container_id, university, current_designation)
);