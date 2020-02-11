package com.vinaysshenoy.quarantine.db.assertions

import org.jdbi.v3.core.mapper.reflect.ColumnName

data class ColumnSpec(
    @ColumnName("name")
    val name: String,

    @ColumnName("type")
    val type: Type,

    @ColumnName("notnull")
    val notNull: Boolean,

    @ColumnName("pk")
    val isPrimaryKey: Boolean
) {

    enum class Type {
        NULL, INTEGER, REAL, TEXT, BLOB
    }
}