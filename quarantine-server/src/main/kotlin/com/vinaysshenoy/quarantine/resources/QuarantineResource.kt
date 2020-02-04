package com.vinaysshenoy.quarantine.resources

import com.vinaysshenoy.quarantine.dao.QuarantineDao
import com.vinaysshenoy.quarantine.dao.TestCase
import com.vinaysshenoy.quarantine.dao.TestRun
import com.vinaysshenoy.quarantine.dao.TestRunResult
import com.vinaysshenoy.quarantine.resources.payloads.TestCasePayload
import com.vinaysshenoy.quarantine.resources.views.TestStatsView
import java.time.Clock
import java.time.Instant
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.MediaType.TEXT_HTML
import javax.ws.rs.core.Response

@Path("/quarantine")
@Produces(value = [APPLICATION_JSON, TEXT_HTML])
@Consumes(APPLICATION_JSON)
class QuarantineResource(
    private val clock: Clock,
    private val quarantineDao: QuarantineDao
) {

    @POST
    fun reportTestRun(@NotNull @Valid testCasePayloads: List<TestCasePayload>): Response {
        val testRun = TestRun(timestamp = Instant.now(clock))
        val testCases = testCasePayloads
            .map { TestCase(className = it.testClass, testName = it.testName) }
        val results = testCasePayloads
            .map {
                TestRunResult(
                    testCaseClassName = it.testClass,
                    testCaseTestName = it.testName,
                    flakyStatus = it.flakyStatus
                )
            }

        quarantineDao.recordTestRun(testRun, testCases, results)

        return Response.ok().build()
    }

    @GET
    fun stats(): TestStatsView {
        return TestStatsView(quarantineDao.stats().sortedByDescending { it.flakinessRate })
    }
}