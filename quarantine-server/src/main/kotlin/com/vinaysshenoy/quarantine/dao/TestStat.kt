package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class TestStat(
    @ColumnName("class")
    val className: String,

    @ColumnName("name")
    val testName: String,

    @ColumnName("flakiness")
    val flakinessRate: Float
)