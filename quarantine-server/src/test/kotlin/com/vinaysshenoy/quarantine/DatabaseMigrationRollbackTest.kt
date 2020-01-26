package com.vinaysshenoy.quarantine

import ch.qos.logback.classic.Level
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.sql.DriverManager

class DatabaseMigrationRollbackTest {

    private lateinit var liquibase: Liquibase

    @BeforeEach
    fun setUp() {
        liquibase = Liquibase(
            "migrations.sql",
            ClassLoaderResourceAccessor(),
            JdbcConnection(DriverManager.getConnection("jdbc:sqlite::memory:"))
        )

        val logger = LoggerFactory.getLogger("liquibase")
        (logger as ch.qos.logback.classic.Logger).level = Level.INFO
    }

    @AfterEach
    fun tearDown() {
        liquibase.database.close()
    }

    @Test
    fun `database migrations can be rolled back`() {
        val contexts = Contexts("")
        val labels = LabelExpression()

        liquibase.changeLogParameters.apply {
            setContexts(contexts)
            this.labels = labels
        }

        with(liquibase) {
            update(null, contexts, labels)
            rollback(databaseChangeLog.changeSets.size, contexts, labels)
            update(null, contexts, labels)
        }
    }
}