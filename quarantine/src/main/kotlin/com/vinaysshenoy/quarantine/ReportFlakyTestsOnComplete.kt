package com.vinaysshenoy.quarantine

import java.util.concurrent.atomic.AtomicBoolean

class ReportFlakyTestsOnComplete(
    private val repository: TestRepository,
    private val config: Config
) : Thread("report-flaky-tests-thread") {

    companion object {
        private val hasBeenSetup = AtomicBoolean(false)

        fun setup(
            repository: TestRepository,
            config: Config
        ) {
            if (!hasBeenSetup.get()) {
                val reportFlakyTests =
                    ReportFlakyTestsOnComplete(repository, config)
                Runtime.getRuntime().addShutdownHook(reportFlakyTests)

                hasBeenSetup.set(true)
            }
        }
    }

    private val logger = logger<ReportFlakyTestsOnComplete>()

    init {
        logger.info("CONFIG: $config")
    }

    override fun run() {
        logger.info("FLAKY: ${repository.results().filter(TestDescriptor::isFlaky).map { it.testMethod }}")
        logger.info("NOT FLAKY: ${repository.results().filter { !it.isFlaky }.map { it.testMethod }}")
    }
}