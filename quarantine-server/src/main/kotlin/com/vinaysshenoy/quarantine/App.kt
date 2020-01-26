package com.vinaysshenoy.quarantine

import io.dropwizard.Application
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.slf4j.LoggerFactory

class App : Application<AppConfiguration>() {

    private val logger = LoggerFactory.getLogger(App::class.java)

    override fun initialize(bootstrap: Bootstrap<AppConfiguration>) {
        bootstrap.addBundle(object : MigrationsBundle<AppConfiguration>() {

            override fun getDataSourceFactory(configuration: AppConfiguration) = configuration.database

            override fun getMigrationsFileName() = "migrations.sql"
        })
    }

    override fun run(configuration: AppConfiguration, environment: Environment) {
        logger.info("RUNNING [${configuration.appName}]")

        with(JdbiFactory()) {
            val jdbi = build(environment, configuration.database, "app-db")
            // todo: Add resources
        }
    }
}

fun main(args: Array<String>) {
    App().run(*args)
}