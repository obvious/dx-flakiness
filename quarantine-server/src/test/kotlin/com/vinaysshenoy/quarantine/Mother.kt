package com.vinaysshenoy.quarantine

import com.vinaysshenoy.quarantine.dao.FlakyStatus
import com.vinaysshenoy.quarantine.dao.TestCase
import com.vinaysshenoy.quarantine.dao.TestRun
import com.vinaysshenoy.quarantine.dao.TestRunResult
import java.time.Instant

fun testRun(
    id: Int = 1,
    timestamp: Instant = Instant.parse("2018-01-01T00:00:00Z")
): TestRun = TestRun(id, timestamp)

fun testCase(
    id: Int = 1,
    className: String = "BestTest",
    testName: String = "best test of the century"
): TestCase = TestCase(id, className, testName)

fun testRunResult(
    runId: Int = 1,
    caseId: Int = 1,
    testCaseClassName: String = "BestTest",
    testCaseTestName: String = "best test of the century",
    flakyStatus: FlakyStatus = FlakyStatus.NotFlaky
): TestRunResult = TestRunResult(runId, caseId, testCaseClassName, testCaseTestName, flakyStatus)