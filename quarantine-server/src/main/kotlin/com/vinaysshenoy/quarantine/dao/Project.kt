package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class Project(
    @ColumnName("id")
    val id: Int = -1,

    @ColumnName("slug")
    val slug: String,

    @ColumnName("name")
    val name: String
) {
    fun withId(id: Int): Project = copy(id = id)
}