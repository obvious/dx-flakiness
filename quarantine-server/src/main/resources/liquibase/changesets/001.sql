--liquibase formatted sql

--changeset vinaysshenoy:create-initial-tables dbms:sqlite
CREATE TABLE "Test_Cases" (
    "id" INTEGER PRIMARY KEY,
    "class" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    CONSTRAINT "Test_Cases_class_name_unique" UNIQUE ("class", "name")
);
CREATE TABLE "Test_Runs" (
    "id" INTEGER PRIMARY KEY,
    "timestamp" TEXT NOT NULL
);
CREATE TABLE "Test_Run_Results" (
    "run_id" INTEGER,
    "case_id" INTEGER,
    "flaky_status" TEXT NOT NULL,
    CONSTRAINT "Test_Run_Results_pk" PRIMARY KEY ("run_id", "case_id"),
    CONSTRAINT "Test_Run_Results_run_id_pk" FOREIGN KEY ("run_id") REFERENCES "Test_Runs"("id") ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT "Test_Run_Results_case_id_pk" FOREIGN KEY ("case_id") REFERENCES "Test_Cases"("id") ON DELETE CASCADE ON UPDATE NO ACTION
);
--rollback DROP TABLE "Test_Run_Results";
--rollback DROP TABLE "Test_Runs";
--rollback DROP TABLE "Test_Cases";