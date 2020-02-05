package com.vinaysshenoy.quarantine

import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

class ReportFlakyTestsOnComplete(
    private val repository: TestRepository
) : Thread("report-flaky-tests-thread") {

    companion object {
        private val hasBeenSetup = AtomicBoolean(false)

        fun setup(repository: TestRepository) {
            if (!hasBeenSetup.get()) {
                val reportFlakyTests =
                    ReportFlakyTestsOnComplete(repository)
                Runtime.getRuntime().addShutdownHook(reportFlakyTests)

                hasBeenSetup.set(true)
            }
        }
    }

    private val logger = LoggerFactory.getLogger(ReportFlakyTestsOnComplete::class.java.simpleName)

    override fun run() {
        logger.info("FLAKY: ${repository.results().filter(TestDescriptor::isFlaky).map { it.testMethod }}")
        logger.info("NOT FLAKY: ${repository.results().filter { !it.isFlaky }.map { it.testMethod }}")
    }
}