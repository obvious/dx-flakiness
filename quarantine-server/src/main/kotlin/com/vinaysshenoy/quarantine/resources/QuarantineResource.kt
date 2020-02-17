package com.vinaysshenoy.quarantine.resources

import com.vinaysshenoy.quarantine.dao.*
import com.vinaysshenoy.quarantine.resources.payloads.TestCasePayload
import com.vinaysshenoy.quarantine.resources.views.ProjectsView
import com.vinaysshenoy.quarantine.resources.views.TestStatsView
import io.dropwizard.jersey.errors.ErrorMessage
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
import javax.ws.rs.core.Response.Status.NOT_FOUND

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

    @Path("/{project_slug}/reports")
    @POST
    fun reportTestRun(
        @NotBlank @PathParam("project_slug") projectSlug: String,
        @NotNull @Valid testCasePayloads: List<TestCasePayload>
    ): Response {
        val project = quarantineDao.findProjectBySlug(projectSlug)
        return if (project == null) {
            Response
                .status(NOT_FOUND)
                .entity(ErrorMessage(NOT_FOUND.statusCode, "Could not find project with slug: $projectSlug"))
                .build()
        } else {
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

            quarantineDao.recordTestRun(testRun, testCases, results, projectSlug)

            Response.ok("OK").build()
        }
    }

    @Produces(value = [TEXT_HTML, APPLICATION_JSON])
    @Path("/{project_slug}/reports")
    @GET
    fun stats(
        @NotBlank @PathParam("project_slug") projectSlug: String
    ): TestStatsView {
        val stats = quarantineDao.stats(projectSlug).sortedByDescending { it.flakinessRate }
        return TestStatsView.fromStats(stats)
    }
}