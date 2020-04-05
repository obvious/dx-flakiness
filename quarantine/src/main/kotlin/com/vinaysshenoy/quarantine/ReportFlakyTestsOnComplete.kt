package com.vinaysshenoy.quarantine

import java.util.concurrent.atomic.AtomicBoolean

class ReportFlakyTestsOnComplete : Thread("report-flaky-tests-thread") {

    companion object {
        private val hasBeenSetup = AtomicBoolean(false)

        fun setup() {
            val isRunningOnJvm = !Quarantine.isOnAndroid
            val hasNotAlreadyBeenSetup = !hasBeenSetup.get()
            val isFlakyTestDetectionEnabled = Quarantine.isEnabled

            if (isRunningOnJvm && hasNotAlreadyBeenSetup && isFlakyTestDetectionEnabled) {
                val reportFlakyTests = ReportFlakyTestsOnComplete()
                Runtime.getRuntime().addShutdownHook(reportFlakyTests)

                hasBeenSetup.set(true)
            }
        }
    }

    private val logger = logger<ReportFlakyTestsOnComplete>()

    override fun run() {
        Quarantine.pushResults()
    }
}