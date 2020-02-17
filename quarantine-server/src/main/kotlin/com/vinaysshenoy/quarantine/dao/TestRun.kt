package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.time.Instant

data class TestRun(
    @ColumnName("id")
    val id: Int = -1,

    @ColumnName("project_id")
    val projectId: Int = -1,

    @ColumnName("timestamp")
    val timestamp: Instant
)