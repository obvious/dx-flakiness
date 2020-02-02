package com.vinaysshenoy.quarantine

import ch.qos.logback.classic.Level
import liquibase.logging.Logger
import liquibase.logging.LoggerContext
import liquibase.logging.core.AbstractLoggerFactory
import liquibase.logging.core.Slf4JLoggerFactory
import org.slf4j.LoggerFactory

/**
 * Liquibase is quite noisy in tests and it's hard to configure liquibase logs globally for tests. This is a hack that
 * lets us globally set the log level only for liquibase in tests
 **/
class LiquibaseLoggerFactory(
    private val logLevel: Level = Level.INFO
) : AbstractLoggerFactory() {

    private val actualLoggerFactory = Slf4JLoggerFactory()

    override fun getLog(clazz: Class<*>?): Logger {
        val logger = actualLoggerFactory.getLog(clazz)

        LoggerFactory.getLogger(clazz).apply {
            (this as ch.qos.logback.classic.Logger).level = logLevel
        }

        return logger
    }

    override fun pushContext(key: String?, `object`: Any?): LoggerContext {
        return actualLoggerFactory.pushContext(key, `object`)
    }

    override fun close() {
        actualLoggerFactory.close()
    }
}