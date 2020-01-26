package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class TestRunResults(
    @ColumnName("run_id")
    val runId: Int,

    @ColumnName("case_id")
    val caseId: Int,

    @ColumnName("flaky_status")
    val flakyStatus: FlakyStatus
)