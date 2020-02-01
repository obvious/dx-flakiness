package com.vinaysshenoy.quarantine.mappers.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.Types
import java.time.Instant

object JdbiInstant {

    fun install(jdbi: Jdbi) {
        jdbi.apply {
            registerArgument(ArgumentFactory())
            registerColumnMapper(ColumnMapper())
        }
    }

    private class ColumnMapper : org.jdbi.v3.core.mapper.ColumnMapper<Instant> {

        override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext): Instant {
            return Instant.parse(r.getString(columnNumber))
        }
    }

    private class ArgumentFactory : org.jdbi.v3.core.argument.AbstractArgumentFactory<Instant>(Types.VARCHAR) {

        override fun build(value: Instant, config: ConfigRegistry): Argument {
            return Argument { position, statement, _ ->
                statement.setString(position, value.toString())
            }
        }
    }
}