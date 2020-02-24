package com.vinaysshenoy.quarantine

import java.net.URL
import java.util.*

data class Config(
    val enabled: Boolean,
    val endpoint: URL,
    val slug: String
) {
    companion object {
        private const val DEFAULT_CONFIG_FILE = "quarantine.properties"
        private const val CONFIG_FILE_ENV = "com.vinaysshenoy.quarantine.configFile"
        private const val ENABLED_KEY = "enabled"
        private const val ENDPOINT_KEY = "serviceEndpoint"
        private const val PROJECT_SLUG_KEY = "slug"

        fun read(classLoader: ClassLoader): Config {
            val configFile = System.getenv().getOrDefault(CONFIG_FILE_ENV, DEFAULT_CONFIG_FILE)
            val properties = Properties().apply {
                load(classLoader.getResourceAsStream(configFile))
            }

            if (PROJECT_SLUG_KEY !in properties.keys) {
                throw IllegalStateException("Could not find required key [$PROJECT_SLUG_KEY] in $configFile")
            }

            val enabled = properties.getProperty(ENABLED_KEY, "false")!!.toBoolean()

            return Config(
                enabled = enabled,
                endpoint = URL(properties.getProperty(ENDPOINT_KEY, "http://localhost:80")),
                slug = properties.getProperty(PROJECT_SLUG_KEY)
            )
        }
    }
}