package com.vinaysshenoy.quarantine.dbqueries

import com.vinaysshenoy.quarantine.dao.FlakyStatus.Flaky
import com.vinaysshenoy.quarantine.dao.FlakyStatus.NotFlaky
import com.vinaysshenoy.quarantine.dao.QuarantineDao
import com.vinaysshenoy.quarantine.dao.TestStat
import com.vinaysshenoy.quarantine.extensions.JdbiObjectParameterResolver
import com.vinaysshenoy.quarantine.project
import com.vinaysshenoy.quarantine.testCase
import com.vinaysshenoy.quarantine.testRun
import com.vinaysshenoy.quarantine.testRunResult
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.*

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
                flakyStatus = Flaky
            ),
            testRunResult(
                runId = -1,
                caseId = -1,
                testCaseClassName = "TestClass1",
                testCaseTestName = "best test 2",
                flakyStatus = NotFlaky
            ),
            testRunResult(
                runId = -1,
                caseId = -1,
                testCaseClassName = "TestClass2",
                testCaseTestName = "best test 1",
                flakyStatus = Flaky
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
                    flakyStatus = Flaky
                ),
                testRunResult(
                    runId = 1,
                    caseId = 2,
                    testCaseClassName = "TestClass1",
                    testCaseTestName = "best test 2",
                    flakyStatus = NotFlaky
                ),
                testRunResult(
                    runId = 1,
                    caseId = 3,
                    testCaseClassName = "TestClass2",
                    testCaseTestName = "best test 1",
                    flakyStatus = Flaky
                )
            )
    }

    @Test
    fun `querying the test stats should return a list of all test cases sorted by the most flaky`(quarantineDao: QuarantineDao) {
        // given
        val firstTestRun = Triple(
            testRun(),
            listOf(
                testCase(className = "Class 1", testName = "test 1"),
                testCase(className = "Class 1", testName = "test 2"),
                testCase(className = "Class 2", testName = "test 1")
            ),
            listOf(
                testRunResult(
                    testCaseClassName = "Class 1",
                    testCaseTestName = "test 1",
                    flakyStatus = Flaky
                ),
                testRunResult(
                    testCaseClassName = "Class 1",
                    testCaseTestName = "test 2",
                    flakyStatus = NotFlaky
                ),
                testRunResult(
                    testCaseClassName = "Class 2",
                    testCaseTestName = "test 1",
                    flakyStatus = Flaky
                )
            )
        )

        val secondTestRun = Triple(
            testRun(),
            listOf(
                testCase(className = "Class 1", testName = "test 1"),
                testCase(className = "Class 1", testName = "test 2"),
                testCase(className = "Class 2", testName = "test 1")
            ),
            listOf(
                testRunResult(
                    testCaseClassName = "Class 1",
                    testCaseTestName = "test 1",
                    flakyStatus = Flaky
                ),
                testRunResult(
                    testCaseClassName = "Class 1",
                    testCaseTestName = "test 2",
                    flakyStatus = Flaky
                ),
                testRunResult(
                    testCaseClassName = "Class 2",
                    testCaseTestName = "test 1",
                    flakyStatus = NotFlaky
                )
            )
        )

        val thirdTestRun = Triple(
            testRun(),
            listOf(
                testCase(className = "Class 1", testName = "test 1"),
                testCase(className = "Class 1", testName = "test 2"),
                testCase(className = "Class 2", testName = "test 1"),
                testCase(className = "Class 2", testName = "test 2"),
                testCase(className = "Class 3", testName = "test 1")
            ),
            listOf(
                testRunResult(
                    testCaseClassName = "Class 1",
                    testCaseTestName = "test 1",
                    flakyStatus = Flaky
                ),
                testRunResult(
                    testCaseClassName = "Class 1",
                    testCaseTestName = "test 2",
                    flakyStatus = Flaky
                ),
                testRunResult(
                    testCaseClassName = "Class 2",
                    testCaseTestName = "test 1",
                    flakyStatus = NotFlaky
                ),
                testRunResult(
                    testCaseClassName = "Class 2",
                    testCaseTestName = "test 2",
                    flakyStatus = NotFlaky
                ),
                testRunResult(
                    testCaseClassName = "Class 3",
                    testCaseTestName = "test 1",
                    flakyStatus = Flaky
                )
            )
        )

        listOf(
            firstTestRun,
            secondTestRun,
            thirdTestRun
        ).forEach { (testRun, testCases, results) -> quarantineDao.recordTestRun(testRun, testCases, results) }

        // when
        val stats = quarantineDao.stats()

        // then
        expectThat(stats).containsExactly(
            TestStat(
                className = "Class 1",
                testName = "test 1",
                flakinessRate = 1F
            ),
            TestStat(
                className = "Class 1",
                testName = "test 2",
                flakinessRate = 0.67F
            ),
            TestStat(
                className = "Class 2",
                testName = "test 1",
                flakinessRate = 0.33F
            ),
            TestStat(
                className = "Class 2",
                testName = "test 2",
                flakinessRate = 0F
            ),
            TestStat(
                className = "Class 3",
                testName = "test 1",
                flakinessRate = 1F
            )
        )
    }

    @Test
    fun `saving projects should work as expected`(quarantineDao: QuarantineDao) {
        // given
        val myProject = project(
            id = -1,
            slug = "my-terrible-project",
            name = "My Awesome Project"
        )
        val theirProject = project(
            id = -2,
            slug = "their-awesome-project",
            name = "Their Terrible Project"
        )

        // when
        expectThat(quarantineDao.projects()).isEmpty()
        val generatedIdOfMyProject = quarantineDao.createProject(myProject)
        val generatedIdOfTheirProject = quarantineDao.createProject(theirProject)

        // then
        val projects = quarantineDao.projects()
        expectThat(projects)
            .containsExactlyInAnyOrder(
                myProject.withId(generatedIdOfMyProject),
                theirProject.withId(generatedIdOfTheirProject)
            )
    }

    @Test
    fun `fetching an existing project by slug should return the project`(quarantineDao: QuarantineDao) {
        // given
        val existingProjectSlug = "live-project"
        val projectThatWeAreLookingFor = project(id = 1, slug = existingProjectSlug, name = "Woo Hoo")
        val projectThatWeAreNotLookingFor = project(id = 2, slug = "dead-project", name = "Boo Hoo")
        val nonExistentProjectSlug = "castle-in-the-air"
        quarantineDao.createProject(projectThatWeAreLookingFor)
        quarantineDao.createProject(projectThatWeAreNotLookingFor)

        // when
        val foundProject = quarantineDao.findProjectBySlug(existingProjectSlug)

        // then
        expectThat(foundProject) isEqualTo projectThatWeAreLookingFor
        expectThat(quarantineDao.findProjectBySlug(nonExistentProjectSlug)).isNull()
    }
}