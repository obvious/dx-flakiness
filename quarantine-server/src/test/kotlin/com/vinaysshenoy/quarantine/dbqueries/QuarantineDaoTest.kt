package com.vinaysshenoy.quarantine.dbqueries

import com.vinaysshenoy.quarantine.dao.FlakyStatus
import com.vinaysshenoy.quarantine.dao.QuarantineDao
import com.vinaysshenoy.quarantine.extensions.JdbiObjectParameterResolver
import com.vinaysshenoy.quarantine.testCase
import com.vinaysshenoy.quarantine.testRun
import com.vinaysshenoy.quarantine.testRunResult
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEqualTo

@ExtendWith(JdbiObjectParameterResolver::class)
class QuarantineDaoTest {

    @Test
    fun `saving test results should work as expected`(quarantineDao: QuarantineDao) {
        // given
        val testRun = testRun(id = -1)
        val testCases = listOf(
            testCase(
                id = -1,
                className = "TestClass1",
                testName = "best test 1"
            ), testCase(
                id = -1,
                className = "TestClass1",
                testName = "best test 2"
            ), testCase(
                id = -1,
                className = "TestClass2",
                testName = "best test 1"
            )
        )
        val runResults = listOf(
            testRunResult(
                runId = -1,
                caseId = -1,
                testCaseClassName = "TestClass1",
                testCaseTestName = "best test 1",
                flakyStatus = FlakyStatus.Flaky
            ),
            testRunResult(
                runId = -1,
                caseId = -1,
                testCaseClassName = "TestClass1",
                testCaseTestName = "best test 2",
                flakyStatus = FlakyStatus.NotFlaky
            ),
            testRunResult(
                runId = -1,
                caseId = -1,
                testCaseClassName = "TestClass2",
                testCaseTestName = "best test 1",
                flakyStatus = FlakyStatus.Flaky
            )
        )

        // when
        quarantineDao.recordTestRun(testRun, testCases, runResults)

        // then
        expectThat(quarantineDao.testResultsForRunId(1))
            .containsExactlyInAnyOrder(
                testRunResult(
                    runId = 1,
                    caseId = 1,
                    testCaseClassName = "TestClass1",
                    testCaseTestName = "best test 1",
                    flakyStatus = FlakyStatus.Flaky
                ),
                testRunResult(
                    runId = 1,
                    caseId = 2,
                    testCaseClassName = "TestClass1",
                    testCaseTestName = "best test 2",
                    flakyStatus = FlakyStatus.NotFlaky
                ),
                testRunResult(
                    runId = 1,
                    caseId = 3,
                    testCaseClassName = "TestClass2",
                    testCaseTestName = "best test 1",
                    flakyStatus = FlakyStatus.Flaky
                )
            )
    }
}