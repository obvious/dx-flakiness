package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class TestCase(
    @ColumnName("id")
    val id: Int = -1,

    @ColumnName("project_id")
    val projectId: Int = -1,

    @ColumnName("class")
    val className: String,

    @ColumnName("name")
    val testName: String
)