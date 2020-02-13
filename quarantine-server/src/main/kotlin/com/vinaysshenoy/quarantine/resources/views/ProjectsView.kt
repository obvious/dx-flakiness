package com.vinaysshenoy.quarantine.resources.views

import com.vinaysshenoy.quarantine.dao.Project
import io.dropwizard.views.View

data class ProjectsView(
    val projects: List<ProjectViewModel>
): View("projects.mustache", Charsets.UTF_8) {

    companion object {
        fun fromProjects(projects: List<Project>): ProjectsView = ProjectsView(projects.map(::ProjectViewModel))
    }

    data class ProjectViewModel(
        val slug: String,
        val name: String
    ) {
        constructor(project: Project) : this(slug = project.slug, name = project.name)
    }
}