package com.vinaysshenoy.quarantine

import java.util.concurrent.atomic.AtomicBoolean

class ReportFlakyTestsOnComplete(
    private val repository: TestRepository
) : Thread("report-flaky-tests-thread") {

    companion object {
        private val hasBeenSetup = AtomicBoolean(false)

        fun setup(repository: TestRepository) {
            if (!hasBeenSetup.get()) {
                val reportFlakyTests = ReportFlakyTestsOnComplete(repository)
                Runtime.getRuntime().addShutdownHook(reportFlakyTests)

                hasBeenSetup.set(true)
            }
        }
    }

    private val logger = logger<ReportFlakyTestsOnComplete>()

    override fun run() {
        logger.info("FLAKY: ${repository.results().filter(TestDescriptor::isFlaky).map { it.testMethod }}")
        logger.info("NOT FLAKY: ${repository.results().filter { !it.isFlaky }.map { it.testMethod }}")
    }
}