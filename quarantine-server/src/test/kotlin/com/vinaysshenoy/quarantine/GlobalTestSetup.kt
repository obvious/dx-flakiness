package com.vinaysshenoy.quarantine

import liquibase.logging.LogService
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestPlan

class GlobalTestSetup : TestExecutionListener {

    override fun testPlanExecutionStarted(testPlan: TestPlan?) {
        super.testPlanExecutionStarted(testPlan)
        LogService.setLoggerFactory(LiquibaseLoggerFactory())
    }
}