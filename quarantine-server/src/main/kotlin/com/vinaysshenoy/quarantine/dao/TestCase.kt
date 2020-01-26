package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class TestCase(
    @ColumnName("id")
    val id: Int,

    @ColumnName("class")
    val className: String,

    @ColumnName("name")
    val testName: String
)