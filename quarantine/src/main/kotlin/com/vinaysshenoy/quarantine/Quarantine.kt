package com.vinaysshenoy.quarantine

object Quarantine {

    var classLoader: ClassLoader = ClassLoader.getSystemClassLoader()

    val repository: TestRepository by lazy { InMemoryTestRepository.instance(classLoader) }

    val isEnabled: Boolean
        get() = repository.config().enabled
}