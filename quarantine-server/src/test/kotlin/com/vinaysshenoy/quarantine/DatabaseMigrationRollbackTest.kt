package com.vinaysshenoy.quarantine

import com.vinaysshenoy.quarantine.extensions.LiquibaseParameterResolver
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(LiquibaseParameterResolver::class)
class DatabaseMigrationRollbackTest {

    @Test
    fun `database migrations can be rolled back`(liquibase: Liquibase) {
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