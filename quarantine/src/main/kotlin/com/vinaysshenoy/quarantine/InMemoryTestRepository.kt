package com.vinaysshenoy.quarantine

class InMemoryTestRepository(private val config: Config) : TestRepository {

    companion object {
        private lateinit var INSTANCE: InMemoryTestRepository

        @JvmOverloads
        fun instance(classLoader: ClassLoader = ClassLoader.getSystemClassLoader()): TestRepository {
            if (::INSTANCE.isInitialized.not()) {
                synchronized(::INSTANCE) {
                    if (::INSTANCE.isInitialized.not()) {
                        INSTANCE = InMemoryTestRepository(Config.read(classLoader))
                    }
                }
            }

            return INSTANCE
        }
    }

    private val logger = logger<InMemoryTestRepository>()

    private var tests: List<TestDescriptor> = emptyList()

    private var results: List<TestDescriptor> = emptyList()

    init {
        logger.info("Config: $config")
    }

    override fun add(descriptors: List<TestDescriptor>) {
        tests = tests + descriptors
    }

    override fun record(clazz: String, method: String, isFlaky: Boolean) {
        val test = findTest(clazz, method)

        if (test != null) {
            val result = if (isFlaky) test.asFlaky() else test.asNotFlaky()
            results = results + result
        }
    }

    override fun results(): List<TestDescriptor> = results

    override fun pushResultsToCloud() {
    }

    override fun config(): Config {
        return config
    }

    private fun findTest(clazz: String, method: String): TestDescriptor? {
        return tests.find { it.testClass == clazz && it.testMethod == method }
    }
}