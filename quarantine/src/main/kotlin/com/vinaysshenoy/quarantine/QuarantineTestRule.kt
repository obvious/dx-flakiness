package com.vinaysshenoy.quarantine

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class QuarantineTestRule : TestRule {

    private val repository: TestRepository by lazy { InMemoryTestRepository.instance() }

    init {
        ReportFlakyTestsOnComplete.setup(repository)
    }

    override fun apply(statement: Statement, description: Description): Statement {
        return QuarantinedStatement(
            repository = repository,
            flakyTestRetryCount = 10,
            base = statement,
            description = description
        )
    }

    private class QuarantinedStatement(
        private val repository: TestRepository,
        private val flakyTestRetryCount: Int,
        private val base: Statement,
        private val description: Description
    ): Statement() {

        override fun evaluate() {
            val descriptors = TestDescriptor.fromDescription(description)
            repository.add(descriptors)

            if (description.isTest) {
                val testClazzName = description.className
                val testMethodName = description.methodName

                try {
                    base.evaluate()
                    // If it passes, mark it as not-flaky
                    repository.record(testClazzName, testMethodName, false)
                } catch (e: Throwable) {
                    // This test having failed once need not mean that is flaky. We will try running the test some more times
                    // and see if they pass in any of those. In which case, we will mark them as flaky.
                    val testRunCount = generateSequence(1) { testRetryNumber ->
                        when {
                            testRetryNumber > flakyTestRetryCount -> null
                            else -> {
                                val hasPassedOnRetry = retryAndReturnException(base) == null

                                if (hasPassedOnRetry) null else testRetryNumber + 1
                            }
                        }
                    }.last()

                    if (testRunCount == flakyTestRetryCount) {
                        // We have tested it for all retry counts and it has failed each time, so it is not a flaky test
                        repository.record(testClazzName, testMethodName, false)

                        // Only report failed, non-flaky tests as failures
                        throw e
                    } else {
                        repository.record(testClazzName, testMethodName, true)
                    }
                }
            }
        }

        private fun retryAndReturnException(statement: Statement): Throwable? {
            var failure: Throwable? = null

            try {
                statement.evaluate()
            } catch (e: Throwable) {
                failure = e
            }

            return failure
        }
    }
}