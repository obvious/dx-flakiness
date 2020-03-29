package com.vinaysshenoy.quarantine

import org.junit.internal.AssumptionViolatedException
import org.junit.internal.runners.model.EachTestNotifier
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod

class QuarantineTestRunner(clazz: Class<*>) : BlockJUnit4ClassRunner(clazz) {

    private val repository: TestRepository by lazy { InMemoryTestRepository.instance(ClassLoader.getSystemClassLoader()) }

    init {
        ReportFlakyTestsOnComplete.setup(repository)
    }

    private val flakyTestRetryCount = 10

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
        runChildWithFlakinessChecks(method, notifier)
    }

    private fun runChildWithFlakinessChecks(method: FrameworkMethod, notifier: RunNotifier) {
        val description = describeChild(method)
        if (isIgnored(method)) {
            notifier.fireTestIgnored(description)
        } else {
            runLeaf(
                method = method,
                description = description,
                notifier = EachTestNotifier(notifier, description)
            )
        }
    }

    private fun runLeaf(
        method: FrameworkMethod,
        description: Description,
        notifier: EachTestNotifier
    ) {
        val statement = QuarantinedStatement(
            repository = repository,
            flakyTestRetryCount = flakyTestRetryCount,
            base = methodBlock(method),
            description = description
        )

        notifier.fireTestStarted()

        try {
            statement.evaluate()
        } catch (e: AssumptionViolatedException) {
            notifier.addFailedAssumption(e)
        } catch (e: Throwable) {
            notifier.addFailure(e)
        } finally {
            notifier.fireTestFinished()
        }
    }
}