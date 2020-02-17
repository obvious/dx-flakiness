--liquibase formatted sql

--changeset vinaysshenoy:create-initial-tables dbms:sqlite
CREATE TABLE Projects (
    id INTEGER NOT NULL PRIMARY KEY,
    slug TEXT NOT NULL,
    name TEXT NOT NULL,
    CONSTRAINT Projects_slug_unique UNIQUE (slug)
);

CREATE TABLE Test_Cases (
    id INTEGER PRIMARY KEY,
    project_id INTEGER,
    class TEXT NOT NULL,
    name TEXT NOT NULL,
    CONSTRAINT FK_Test_Cases_project_id__Projects_id FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UNQ_Test_Cases_class_name UNIQUE (project_id, class, name)
);

CREATE TABLE Test_Runs (
    id INTEGER PRIMARY KEY,
    project_id INTEGER,
    timestamp TEXT NOT NULL,
    CONSTRAINT FK_Test_Runs_project_id__Projects_id FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Test_Run_Results (
    run_id INTEGER,
    case_id INTEGER,
    flaky_status TEXT NOT NULL,
    CONSTRAINT PK_Test_Run_Results_run_id_case_id PRIMARY KEY (run_id, case_id),
    CONSTRAINT FK_Test_Run_Results_run_id__Test_Runs_id FOREIGN KEY (run_id) REFERENCES Test_Runs(id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT FK_Test_Run_Results_case_id__Test_Cases_id FOREIGN KEY (case_id) REFERENCES Test_Cases(id) ON DELETE CASCADE ON UPDATE NO ACTION
);

--rollback DROP TABLE Test_Run_Results;
--rollback DROP TABLE Test_Runs;
--rollback DROP TABLE Test_Cases;
--rollback DROP TABLE Projects;