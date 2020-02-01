package com.vinaysshenoy.quarantine.dao

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class TestRunResult(
    @ColumnName("run_id")
    val runId: Int = -1,

    @ColumnName("case_id")
    val caseId: Int = -1,

    @ColumnName("class")
    val testCaseClassName: String,

    @ColumnName("name")
    val testCaseTestName: String,

    @ColumnName("flaky_status")
    val flakyStatus: FlakyStatus
)