package com.vinaysshenoy.quarantine

import io.dropwizard.Application
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.slf4j.LoggerFactory

class App : Application<AppConfiguration>() {

    private val logger = LoggerFactory.getLogger(App::class.java)

    override fun initialize(bootstrap: Bootstrap<AppConfiguration>) {
        bootstrap.apply {
            addBundle(object : MigrationsBundle<AppConfiguration>() {

                override fun getDataSourceFactory(configuration: AppConfiguration) = configuration.database

                override fun getMigrationsFileName() = "migrations.sql"
            })

            addBundle(JdbiExceptionsBundle())
        }
    }

    override fun run(configuration: AppConfiguration, environment: Environment) {
        logger.info("RUNNING [${configuration.appName}]")

        with(JdbiFactory()) {
            val jdbi = build(environment, configuration.database, "app-db").apply {
                installPlugin(KotlinPlugin())
            }

            // todo: Add resources
        }
    }
}

fun main(args: Array<String>) {
    App().run(*args)
}