package com.vinaysshenoy.quarantine

import com.vinaysshenoy.quarantine.dao.*
import java.time.Instant

fun testRun(
    id: Int = 1,
    projectId: Int = 1,
    timestamp: Instant = Instant.parse("2018-01-01T00:00:00Z")
): TestRun = TestRun(
    id = id,
    projectId = projectId,
    timestamp = timestamp
)

fun testCase(
    id: Int = 1,
    projectId: Int = 1,
    className: String = "BestTest",
    testName: String = "best test of the century"
): TestCase = TestCase(
    id = id,
    projectId = projectId,
    className = className,
    testName = testName
)

fun testRunResult(
    runId: Int = 1,
    caseId: Int = 1,
    testCaseClassName: String = "BestTest",
    testCaseTestName: String = "best test of the century",
    flakyStatus: FlakyStatus = FlakyStatus.NotFlaky
): TestRunResult = TestRunResult(
    runId = runId,
    caseId = caseId,
    testCaseClassName = testCaseClassName,
    testCaseTestName = testCaseTestName,
    flakyStatus = flakyStatus
)

fun project(
    id: Int = 1,
    slug: String = "project",
    name: String = "What am I doing?"
): Project = Project(
    id = id,
    slug = slug,
    name = name
)