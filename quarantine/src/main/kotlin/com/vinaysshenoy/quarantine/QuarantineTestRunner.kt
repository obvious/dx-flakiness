package com.vinaysshenoy.quarantine

import org.junit.runner.Description
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.slf4j.LoggerFactory

class QuarantineTestRunner(clazz: Class<*>) : BlockJUnit4ClassRunner(clazz) {

    private val logger = LoggerFactory.getLogger(QuarantineTestRunner::class.java.simpleName)

    override fun run(notifier: RunNotifier) {
        notifier.addListener(object : RunListener() {
            override fun testSuiteStarted(description: Description) {
                logger.info("Start test run: $description with ${description.testCount()} tests")
                val descriptors = TestDescriptor.fromDescription(description)
                logger.info("$descriptors")
                super.testSuiteStarted(description)
            }

            override fun testSuiteFinished(description: Description?) {
                super.testSuiteFinished(description)
                logger.info("Completed test run!")
            }
        })
        super.run(notifier)
    }

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
        logger.info("${method.declaringClass.canonicalName} -> ${method.name}")
        super.runChild(method, notifier)
    }
}