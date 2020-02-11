package com.vinaysshenoy.quarantine.db.migrations

import com.vinaysshenoy.quarantine.db.assertions.ForeignKeySpec
import com.vinaysshenoy.quarantine.mappers.jdbi.JdbiInstant
import com.vinaysshenoy.quarantine.path
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import java.sql.Connection
import java.sql.DriverManager

class MigrationHelper(
    private val changesToApply: Int
) {

    init {
        if (changesToApply < 2) {
            throw IllegalArgumentException("Cannot test migration for < $changesToApply changes!")
        }
    }

    private var hasMigrated: Boolean = false

    private val dbConnection: Connection by lazy {
        DriverManager.getConnection("jdbc:sqlite::memory:")
    }

    private val jdbi: Jdbi by lazy {
        Jdbi.create(dbConnection)
            .apply {
                installPlugin(SqlObjectPlugin())
                installPlugin(KotlinPlugin())
                installPlugin(KotlinSqlObjectPlugin())
                JdbiInstant.install(this)

                registerColumnMapper(ForeignKeySpec.Action.Mapper)
            }
    }

    private val liquibase: Liquibase by lazy {
        Liquibase(
            path("liquibase", "changelog.xml"),
            ClassLoaderResourceAccessor(),
            JdbcConnection(dbConnection)
        )
    }

    fun setup(block: (Handle) -> Unit = { _ -> }) {
        if (hasMigrated) throw IllegalStateException("Cannot do setup AFTER migration has completed!")

        liquibase.update(changesToApply - 1, null)

        jdbi.open().use(block)
    }

    fun migrate(verify: (Handle) -> Unit = { _ -> }) {
        if (hasMigrated) throw IllegalStateException("Migration has already been done!")

        liquibase.update(changesToApply, null)
        hasMigrated = true

        jdbi.open().use(verify)
    }

    fun cleanUp() {
        liquibase.database.close()
        if (!dbConnection.isClosed) {
            dbConnection.close()
        }
    }
}