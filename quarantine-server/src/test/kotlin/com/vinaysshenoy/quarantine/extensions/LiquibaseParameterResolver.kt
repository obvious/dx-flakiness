package com.vinaysshenoy.quarantine.extensions

import ch.qos.logback.classic.Level
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver
import org.slf4j.LoggerFactory
import java.sql.DriverManager

class LiquibaseParameterResolver : AfterEachCallback, TypeBasedParameterResolver<Liquibase>() {

    companion object {
        private val EXTENSION_NAMESPACE = ExtensionContext.Namespace.create(Any())
        private val KEY = Any()
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Liquibase {
        cleanUp(extensionContext)

        val liquibase = Liquibase(
            "migrations.sql",
            ClassLoaderResourceAccessor(),
            JdbcConnection(DriverManager.getConnection("jdbc:sqlite::memory:"))
        )

        val logger = LoggerFactory.getLogger("liquibase")
        (logger as ch.qos.logback.classic.Logger).level = Level.INFO

        extensionContext
            .getStore(EXTENSION_NAMESPACE)
            .put(KEY, liquibase)

        return liquibase
    }

    override fun afterEach(context: ExtensionContext) {
        cleanUp(context)
    }

    private fun cleanUp(context: ExtensionContext) {
        context
            .getStore(EXTENSION_NAMESPACE)
            .remove(KEY, Liquibase::class.java)?.database?.close()
    }
}