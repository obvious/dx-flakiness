package com.vinaysshenoy.quarantine

import java.net.URL
import java.util.*

data class Config(
    val enabled: Boolean,
    val endpoint: URL
) {
    companion object {

        private const val CONFIG_FILE = "quarantine.properties"
        private const val ENABLED_KEY = "com.vinaysshenoy.quarantine.enabled"
        private const val ENDPOINT_KEY = "com.vinaysshenoy.quarantine.serviceEndpoint"

        fun read(classLoader: ClassLoader): Config {
            val properties = Properties().apply {
                load(classLoader.getResourceAsStream(CONFIG_FILE))
            }

            val enabled = System.getenv().getOrDefault(ENABLED_KEY, "false")!!.toBoolean()

            return Config(
                enabled = enabled,
                endpoint = URL(properties.getProperty(ENDPOINT_KEY))
            )
        }
    }
}