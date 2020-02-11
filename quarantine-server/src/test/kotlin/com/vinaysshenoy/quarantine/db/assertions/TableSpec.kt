package com.vinaysshenoy.quarantine.db.assertions

data class TableSpec(
    val columns: List<ColumnSpec>,
    val foreignKeys: List<ForeignKeySpec>
)