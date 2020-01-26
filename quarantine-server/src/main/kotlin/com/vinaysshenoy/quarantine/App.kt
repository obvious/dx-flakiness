package com.vinaysshenoy.quarantine

import io.dropwizard.Application
import io.dropwizard.setup.Environment
import org.slf4j.LoggerFactory

class App : Application<AppConfiguration>() {

    private val logger = LoggerFactory.getLogger(App::class.java)

    override fun run(configuration: AppConfiguration, environment: Environment) {
        logger.info("RUNNING [${configuration.appName}]")
    }
}

fun main(args: Array<String>) {
    App().run(*args)
}