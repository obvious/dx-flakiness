package com.vinaysshenoy.quarantine

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

class InMemoryTestRepository(
    private val config: Config
) : TestRepository {

    private val retrofit: Retrofit by lazy {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())

        Retrofit
            .Builder()
            .baseUrl(config.endpoint)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .validateEagerly(true)
            .build()
    }

    private val api: QuarantineApi by lazy { retrofit.create<QuarantineApi>() }

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
        try {
            val response = api.sendTestRun(config.slug, results).execute()

            if (response.isSuccessful) {
                logger.info("Pushed test results to Quarantine successfully!")
            } else {
                logger.info("Response Status Code: ${response.code()}")
                logger.info("${response.body()}")
                logger.error("Could not sent results to Quarantine! Checks logs for more information!")
            }
        } catch (e: Throwable) {
            logger.error("Error sending Quarantine results", e)
        }
    }

    override fun config(): Config {
        return config
    }

    private fun findTest(clazz: String, method: String): TestDescriptor? {
        return tests.find { it.testClass == clazz && it.testMethod == method }
    }
}