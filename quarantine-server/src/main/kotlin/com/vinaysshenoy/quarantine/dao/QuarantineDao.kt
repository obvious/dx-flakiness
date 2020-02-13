package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.transaction.Transaction

interface QuarantineDao {

    @SqlBatch(
        """
        INSERT OR IGNORE INTO Test_Cases (
            class, name
        ) VALUES (
            :className, :testName
        )
        """
    )
    @Transaction
    fun saveTestCases(@BindKotlin testCases: List<TestCase>)

    @SqlUpdate(
        """
        INSERT INTO Test_Runs (timestamp) VALUES (:timestamp)
        """
    )
    @GetGeneratedKeys("id")
    @Transaction
    fun saveTestRun(@BindKotlin testRun: TestRun): Int

    @SqlBatch(
        """
            INSERT INTO Test_Run_Results (
                run_id, case_id, flaky_status
            ) 
            SELECT :runId, id, :flakyStatus FROM Test_Cases
                WHERE class = :testCaseClassName AND name = :testCaseTestName
        """
    )
    @Transaction
    fun saveTestRunResults(runId: Int, @BindKotlin runResults: List<TestRunResult>)

    @SqlQuery(
        """
            SELECT TRR.run_id, TRR.case_id, TC.class, TC.name, TRR.flaky_status
            FROM Test_Run_Results TRR
            INNER JOIN Test_Cases TC ON TRR.case_id = TC.id
            WHERE TRR.run_id = :runId
        """
    )
    fun testResultsForRunId(runId: Int): List<TestRunResult>

    @JvmDefault
    @Transaction
    fun recordTestRun(
        testRun: TestRun,
        testCases: List<TestCase>,
        results: List<TestRunResult>
    ) {
        val runId = saveTestRun(testRun)

        saveTestCases(testCases)

        saveTestRunResults(runId, results)
    }

    @SqlQuery(
        """
            SELECT DISTINCT
                TC.class, 
                TC.name,
                ROUND(
                    IFNULL(
                        (
                            CAST((SELECT count(run_id) FROM Test_Run_Results WHERE case_id = TC.id AND flaky_status = 'Flaky') AS REAL) /
                            CAST((SELECT count(run_id) FROM Test_Run_Results WHERE case_id = TC.id) AS REAL)
                        ), 
                        0.0
                    ), 
                    2
                ) flakiness
            FROM Test_Cases TC INNER JOIN Test_Run_Results TRR ON TRR.case_id = TC.id
            ORDER BY class, name
        """
    )
    fun stats(): List<TestStat>

    @SqlUpdate("""
        INSERT INTO Projects (
            slug, name
        ) VALUES (:slug, :name)
    """)
    @GetGeneratedKeys("id")
    fun createProject(@BindKotlin project: Project): Int

    @SqlQuery("""
        SELECT id, slug, name FROM Projects
    """)
    fun projects(): List<Project>

    @SqlQuery("""
        SELECT * FROM Projects WHERE slug = :slug
    """)
    fun findProjectBySlug(slug: String): Project?
}