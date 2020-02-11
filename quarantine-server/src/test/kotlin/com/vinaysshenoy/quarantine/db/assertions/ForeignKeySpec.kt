package com.vinaysshenoy.quarantine.db.assertions

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

data class ForeignKeySpec(

    @ColumnName("from")
    val from: String,

    @ColumnName("to")
    val to: String,

    @ColumnName("table")
    val parentTable: String,

    @ColumnName("on_update")
    val onUpdate: Action,

    @ColumnName("on_delete")
    val onDelete: Action
) {

    enum class Action {
        NoAction, Restrict, SetNull, SetDefault, Cascade;

        object Mapper : ColumnMapper<Action> {
            override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext): Action {
                return when (val action = r.getString(columnNumber)) {
                    "NO ACTION" -> NoAction
                    "RESTRICT" -> Restrict
                    "SET NULL" -> SetNull
                    "SET DEFAULT" -> SetDefault
                    "CASCADE" -> Cascade
                    else -> throw IllegalArgumentException("Unknown value: $action")
                }
            }
        }
    }
}