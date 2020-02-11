package com.vinaysshenoy.quarantine.db.migrations

import com.vinaysshenoy.quarantine.db.assertions.*
import com.vinaysshenoy.quarantine.db.assertions.ColumnSpec.Type.INTEGER
import com.vinaysshenoy.quarantine.db.assertions.ColumnSpec.Type.TEXT
import com.vinaysshenoy.quarantine.db.assertions.ForeignKeySpec.Action.Cascade
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.api.expect
import strikt.api.expectThat

@DbMigrate(2)
class Migration002Test {

    @Test
    fun `it should create the "Projects" table`(helper: MigrationHelper) {
        helper.setup { handle ->
            expectThat(handle).doesNotHaveTable("Projects")
        }

        helper.migrate { handle ->
            expect {
                that(handle).hasTable("Projects")

                val table = handle.tableSpec("Projects")

                that(table).hasPrimaryKey("id")

                that(table)
                    .hasColumn("id")
                    .isOfType(INTEGER)
                    .isNotNullable()

                that(table)
                    .hasColumn("slug")
                    .isOfType(TEXT)
                    .isNotNullable()

                that(table)
                    .hasColumn("name")
                    .isOfType(TEXT)
                    .isNotNullable()
            }
        }
    }

    @Test
    fun `it should add a foreign key reference to "Projects" in "Test_Cases"`(helper: MigrationHelper) {
        helper.setup { handle ->
            val table = handle.tableSpec("Test_Cases")

            expectThat(table).doesNotHaveColumn("project_id")
        }

        helper.migrate { handle ->
            val table = handle.tableSpec("Test_Cases")

            expectThat(table)
                .hasForeignKeyOn("project_id")
                .to("Projects", "id")
                .onDeleteDoes(Cascade)
                .onUpdateDoes(Cascade)
        }
    }

    @Test
    fun `it should add a foreign key reference to "Projects" in "Test_Runs"`(helper: MigrationHelper) {
        helper.setup { handle ->
            val table = handle.tableSpec("Test_Runs")

            expectThat(table).doesNotHaveColumn("project_id")
        }

        helper.migrate { handle ->
            val table = handle.tableSpec("Test_Runs")

            expectThat(table)
                .hasForeignKeyOn("project_id")
                .to("Projects", "id")
                .onDeleteDoes(Cascade)
                .onUpdateDoes(Cascade)
        }
    }
}