package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.time.Instant

data class TestRun(
    @ColumnName("id")
    val id: Int,

    @ColumnName("timestamp")
    val timestamp: Instant
)