package com.vinaysshenoy.quarantine

import com.vinaysshenoy.quarantine.mappers.jdbi.JdbiInstant
import io.dropwizard.jdbi3.JdbiFactory
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin

class AppJdbiFactory : JdbiFactory() {

    override fun configure(jdbi: Jdbi) {
        super.configure(jdbi)
        jdbi.apply {
            installPlugin(KotlinPlugin())
            installPlugin(KotlinSqlObjectPlugin())
            JdbiInstant.install(this)
        }
    }
}