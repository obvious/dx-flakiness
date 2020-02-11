package com.vinaysshenoy.quarantine.extensions

import com.vinaysshenoy.quarantine.mappers.jdbi.JdbiInstant
import com.vinaysshenoy.quarantine.path
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.sql.Connection
import java.sql.DriverManager

class JdbiObjectParameterResolver : ParameterResolver, AfterEachCallback {

    companion object {
        private val EXTENSION_NAMESPACE = ExtensionContext.Namespace.create(Any())
        private val KEY = Any()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        // This is fine because JDBI will take care of the actual checking in resolveParameter()
        return true
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val dbConnection = DriverManager.getConnection("jdbc:sqlite::memory:")

        setupDatabase(dbConnection, extensionContext)

        val jdbi = Jdbi.create(dbConnection)
            .apply {
                installPlugin(SqlObjectPlugin())
                installPlugin(KotlinPlugin())
                installPlugin(KotlinSqlObjectPlugin())
                JdbiInstant.install(this)
            }

        val type = parameterContext.parameter.type

        return jdbi.onDemand(type)
    }

    override fun afterEach(context: ExtensionContext) {
        cleanUp(context)
    }

    private fun cleanUp(context: ExtensionContext) {
        context
            .getStore(EXTENSION_NAMESPACE)
            .remove(KEY, Liquibase::class.java)?.database?.close()
    }

    private fun setupDatabase(
        connection: Connection,
        context: ExtensionContext
    ) {
        val liquibase = Liquibase(
            path("liquibase", "changelog.xml"),
            ClassLoaderResourceAccessor(),
            JdbcConnection(connection)
        )

        liquibase.update("")

        context
            .getStore(EXTENSION_NAMESPACE)
            .put(KEY, liquibase)
    }
}