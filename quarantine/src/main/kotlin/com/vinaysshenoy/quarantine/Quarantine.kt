package com.vinaysshenoy.quarantine

import kotlin.LazyThreadSafetyMode.PUBLICATION

object Quarantine {

    var classLoader: ClassLoader = ClassLoader.getSystemClassLoader()

    val repository: TestRepository by lazy { InMemoryTestRepository.instance(classLoader) }

    val isEnabled: Boolean by lazy(PUBLICATION) { repository.config().enabled }

    val isOnAndroid: Boolean by lazy(PUBLICATION) { hasAnyAndroidTestClasses() }

    private fun hasAnyAndroidTestClasses(): Boolean {
        val runnerClassNames = setOf(
            "androidx.test.runner.AndroidJUnitRunner",
            "android.support.test.runner.AndroidJUnitRunner"
        )

        return runnerClassNames.any { findClass(it) != null }
    }

    private fun findClass(className: String): Class<*>? {
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            null
        }
    }
}