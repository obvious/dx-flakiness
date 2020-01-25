package com.vinaysshenoy.quarantine

import org.junit.runner.Description
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.slf4j.LoggerFactory

class QuarantineTestRunner(clazz: Class<*>) : BlockJUnit4ClassRunner(clazz) {

    private val logger = LoggerFactory.getLogger(QuarantineTestRunner::class.java.simpleName)

    private val repository: TestRepository = InMemoryTestRepository()

    private val flakyTestRetryCount = 3

    override fun run(notifier: RunNotifier) {
        notifier.addListener(object : RunListener() {
            override fun testSuiteStarted(description: Description) {
                val descriptors = TestDescriptor.fromDescription(description)
                repository.add(descriptors)
                super.testSuiteStarted(description)
            }

            override fun testSuiteFinished(description: Description?) {
                super.testSuiteFinished(description)
                logger.info("Completed test run!")
                logger.info("${repository.results()}")
            }
        })
        super.run(notifier)
    }

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
        val testClazzName = method.declaringClass.canonicalName
        val testMethodName = method.name

        try {
            super.runChild(method, notifier)
            // If it passes, mark it as not-flaky
            repository.record(testClazzName, testMethodName, false)
        } catch (e: Throwable) {
            // This test having failed once need not mean that is flaky. We will try running the test some more times,
            // (currently 2 more times), and see if they pass in any of those. In which case, we will mark them as
            // flaky.
            val testRunCount = generateSequence(1) { testRetryNumber ->
                logger.info("retry #$testRetryNumber for $testClazzName, $testMethodName")
                when {
                    testRetryNumber > flakyTestRetryCount -> null
                    else -> {
                        val hasPassedOnRetry = retryAndReturnException(method, notifier) == null

                        if (hasPassedOnRetry) null else testRetryNumber + 1
                    }
                }
            }.last()

            if (testRunCount == flakyTestRetryCount) {
                // We have tested it for all retry counts and it has failed each time, so it is not a flaky test
                repository.record(testClazzName, testMethodName, false)
            } else {
                repository.record(testClazzName, testMethodName, true)
            }
        }
    }

    private fun retryAndReturnException(method: FrameworkMethod, notifier: RunNotifier): Throwable? {
        var failure: Throwable? = null

        try {
            super.runChild(method, notifier)
        } catch (e: Throwable) {
            failure = e
        }

        return failure
    }
}