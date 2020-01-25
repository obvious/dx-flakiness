package com.vinaysshenoy.quarantine

import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.slf4j.LoggerFactory

class QuarantineTestRunner(clazz: Class<*>): BlockJUnit4ClassRunner(clazz) {

    private val logger = LoggerFactory.getLogger(QuarantineTestRunner::class.java.simpleName)

    override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
        logger.info("${method.declaringClass.canonicalName} -> ${method.name}")
        super.runChild(method, notifier)
    }
}