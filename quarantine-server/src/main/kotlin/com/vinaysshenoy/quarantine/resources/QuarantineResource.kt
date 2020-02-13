package com.vinaysshenoy.quarantine.resources

import com.vinaysshenoy.quarantine.dao.*
import com.vinaysshenoy.quarantine.resources.payloads.TestCasePayload
import com.vinaysshenoy.quarantine.resources.views.ProjectsView
import com.vinaysshenoy.quarantine.resources.views.TestStatsView
import org.hibernate.validator.constraints.Length
import java.net.URI
import java.time.Clock
import java.time.Instant
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.*
import javax.ws.rs.core.Response

@Path("")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
class QuarantineResource(
    private val clock: Clock,
    private val quarantineDao: QuarantineDao
) {

    @Produces(TEXT_HTML)
    @GET
    fun allProjects(): ProjectsView {
        val projects = quarantineDao.projects()

        return ProjectsView.fromProjects(projects)
    }

    @Consumes(APPLICATION_FORM_URLENCODED)
    @POST
    fun createProject(
        @Length(min = 7) @NotBlank @FormParam("slug") slug: String,
        @NotBlank @FormParam("name") name: String
    ): Response {
        quarantineDao.createProject(Project(slug = slug, name = name))

        return Response.seeOther(URI.create("")).build()
    }

    @Path("quarantine")
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

        return Response.ok("OK").build()
    }

    @Produces(TEXT_HTML)
    @Path("quarantine")
    @GET
    fun stats(): TestStatsView {
        val stats = quarantineDao.stats().sortedByDescending { it.flakinessRate }
        return TestStatsView.fromStats(stats)
    }
}