--liquibase formatted sql

--changeset vinaysshenoy:add-projects-table dbms:sqlite
CREATE TABLE "Projects" (
    "id" INTEGER NOT NULL PRIMARY KEY,
    "slug" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    CONSTRAINT "Projects_slug_unique" UNIQUE ("slug")
);

ALTER TABLE "Test_Cases"
    ADD COLUMN "project_id" INTEGER NOT NULL DEFAULT 0
        CONSTRAINT "FK_Test_Cases_project_id__Projects_id" REFERENCES "Projects"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "Test_Runs"
    ADD COLUMN "project_id" INTEGER NOT NULL DEFAULT 0
        CONSTRAINT "FK_Test_Runs_project_id__Projects_id" REFERENCES "Projects"("id") ON DELETE CASCADE ON UPDATE CASCADE;

--rollback ALTER TABLE "Test_Runs" RENAME TO "Test_Runs_BACKUP";
--rollback CREATE TABLE "Test_Runs" ("id" INTEGER PRIMARY KEY, "timestamp" TEXT NOT NULL);
--rollback INSERT INTO "Test_Runs" ("id", "timestamp") SELECT "id", "timestamp" FROM "Test_Runs_BACKUP";
--rollback DROP TABLE "Test_Runs_BACKUP";

--rollback ALTER TABLE "Test_Cases" RENAME TO "Test_Cases_BACKUP";
--rollback CREATE TABLE "Test_Cases" ("id" INTEGER PRIMARY KEY AUTOINCREMENT, "class" TEXT NOT NULL, "name" TEXT NOT NULL, CONSTRAINT "Test_Cases_class_name_unique" UNIQUE ("class", "name"));
--rollback INSERT INTO "Test_Cases" ("id", "class", "name") SELECT "id", "class", "name" FROM "Test_Cases_BACKUP";
--rollback DROP TABLE "Test_Cases_BACKUP";

--rollback DROP TABLE "Projects";