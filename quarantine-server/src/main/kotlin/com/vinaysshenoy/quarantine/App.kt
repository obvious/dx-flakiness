package com.vinaysshenoy.quarantine

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.vinaysshenoy.quarantine.resources.QuarantineResource
import io.dropwizard.Application
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.views.ViewBundle
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.slf4j.LoggerFactory
import java.time.Clock

class App : Application<AppConfiguration>() {

    private val logger = LoggerFactory.getLogger(App::class.java)

    override fun initialize(bootstrap: Bootstrap<AppConfiguration>) {
        bootstrap.apply {
            addBundle(object : MigrationsBundle<AppConfiguration>() {

                override fun getDataSourceFactory(configuration: AppConfiguration) = configuration.database

                override fun getMigrationsFileName() = path("liquibase", "changelog.xml")
            })

            addBundle(JdbiExceptionsBundle())
            addBundle(ViewBundle())
        }
    }

    override fun run(configuration: AppConfiguration, environment: Environment) {
        logger.info("RUNNING [${configuration.appName}]")

        val clock = Clock.systemUTC()

        environment.objectMapper.registerModule(KotlinModule())

        val jdbi = AppJdbiFactory().build(environment, configuration.database, "app-db")

        environment.jersey().register(QuarantineResource(clock, jdbi.onDemand()))
    }
}

fun main(args: Array<String>) {
    App().run(*args)
}