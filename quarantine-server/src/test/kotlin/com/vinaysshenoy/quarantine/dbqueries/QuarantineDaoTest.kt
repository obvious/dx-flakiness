package com.vinaysshenoy.quarantine.dbqueries

import ch.qos.logback.classic.Level
import com.vinaysshenoy.quarantine.dao.FlakyStatus
import com.vinaysshenoy.quarantine.dao.QuarantineDao
import com.vinaysshenoy.quarantine.testCase
import com.vinaysshenoy.quarantine.testRun
import com.vinaysshenoy.quarantine.testRunResult
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEqualTo
import java.sql.Connection
import java.sql.DriverManager

class QuarantineDaoTest {

    private val dbConnection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")

    private val jdbi: Jdbi = Jdbi.create(dbConnection)
        .apply {
            installPlugin(SqlObjectPlugin())
            installPlugin(KotlinPlugin())
            installPlugin(KotlinSqlObjectPlugin())
        }

    private val quarantineDao: QuarantineDao by lazy(LazyThreadSafetyMode.NONE) {
        jdbi.onDemand<QuarantineDao>()
    }

    @BeforeEach
    fun setUp() {
        val liquibase = Liquibase(
            "migrations.sql",
            ClassLoaderResourceAccessor(),
            JdbcConnection(dbConnection)
        )

        val logger = LoggerFactory.getLogger("liquibase")
        (logger as ch.qos.logback.classic.Logger).level = Level.OFF

        liquibase.update("")
    }

    @Test
    fun `saving test results should work as expected`() {
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

        val runId = quarantineDao.saveTestRun(testRun)
        expectThat(runId).isEqualTo(1)
        quarantineDao.saveTestCases(testCases)

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
        quarantineDao.saveTestRunResults(runId, runResults)

        // then
        expectThat(quarantineDao.testResultsForRunId(runId))
            .containsExactlyInAnyOrder(
                testRunResult(
                    runId = runId,
                    caseId = 1,
                    testCaseClassName = "TestClass1",
                    testCaseTestName = "best test 1",
                    flakyStatus = FlakyStatus.Flaky
                ),
                testRunResult(
                    runId = runId,
                    caseId = 2,
                    testCaseClassName = "TestClass1",
                    testCaseTestName = "best test 2",
                    flakyStatus = FlakyStatus.NotFlaky
                ),
                testRunResult(
                    runId = runId,
                    caseId = 3,
                    testCaseClassName = "TestClass2",
                    testCaseTestName = "best test 1",
                    flakyStatus = FlakyStatus.Flaky
                )
            )
    }
}