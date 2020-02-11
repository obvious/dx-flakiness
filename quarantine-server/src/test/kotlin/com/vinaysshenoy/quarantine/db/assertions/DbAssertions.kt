package com.vinaysshenoy.quarantine.db.assertions

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import strikt.api.Assertion

fun Assertion.Builder<Handle>.doesNotHaveTable(tableName: String): Assertion.Builder<Handle> {
    assert("does not have table '$tableName'") { handle ->
        val countOfRecords =
            handle.select("""select count(tbl_name) from sqlite_master where tbl_name = ?""", tableName)
                .mapTo<Int>()
                .one()

        if (countOfRecords == 0) pass() else fail("found table '$tableName'")
    }

    return this
}

fun Assertion.Builder<Handle>.hasTable(tableName: String): Assertion.Builder<Handle> {
    assert("has table '$tableName'") { handle ->
        val countOfRecords =
            handle.select("""select count(tbl_name) from sqlite_master where tbl_name = ?""", tableName)
                .mapTo<Int>()
                .one()

        if (countOfRecords > 0) pass() else fail("did not find table '$tableName'")
    }

    return this
}

fun Assertion.Builder<ColumnSpec>.isOfType(type: ColumnSpec.Type): Assertion.Builder<ColumnSpec> {
    assert("is ${type.name}") { spec ->
        if (spec.type == type) pass() else fail("was ${spec.type}")
    }

    return this
}

fun Assertion.Builder<ColumnSpec>.isNullable(): Assertion.Builder<ColumnSpec> {
    assert("is nullable") { spec ->
        if (spec.notNull) fail("was not nullable") else pass()
    }

    return this
}

fun Assertion.Builder<ColumnSpec>.isNotNullable(): Assertion.Builder<ColumnSpec> {
    assert("is not nullable") { spec ->
        if (spec.notNull) pass() else fail("was nullable")
    }

    return this
}

fun Assertion.Builder<TableSpec>.hasColumn(name: String): Assertion.Builder<ColumnSpec> {
    assert("has column '$name'") { spec ->
        val foundColumn = spec.columns.find { it.name == name }

        if (foundColumn == null) fail("did not find column '$name'") else pass()
    }

    return get { columns.find { it.name == name }!! }
}

fun Assertion.Builder<TableSpec>.doesNotHaveColumn(name: String): Assertion.Builder<TableSpec> {
    assert("does not have column '$name'") { spec ->
        val foundColumn = spec.columns.find { it.name == name }

        if (foundColumn != null) fail("found column '$name'") else pass()
    }

    return this
}

fun Assertion.Builder<TableSpec>.hasPrimaryKey(vararg names: String): Assertion.Builder<TableSpec> {
    assert("has primary keys [${names.joinToString()}]") { spec ->
        val foundColumns = spec.columns
            .filter { it.isPrimaryKey }
            .map { it.name }
            .toSet()

        if (names.toSet() == foundColumns) pass() else fail("found primary keys [${foundColumns.joinToString()}]")
    }

    return this
}

fun Assertion.Builder<TableSpec>.hasForeignKeyOn(name: String): Assertion.Builder<ForeignKeySpec> {
    assert("has foreign key '$name'") { spec ->
        val foundForeignKey = spec.foreignKeys.find { it.from == name }

        if (foundForeignKey == null) fail("did not find foreign key on column '$name'") else pass()
    }

    return get { foreignKeys.find { it.from == name }!! }
}

fun Assertion.Builder<ForeignKeySpec>.to(table: String, column: String): Assertion.Builder<ForeignKeySpec> {
    assert("on '$table'.'$column'") { spec ->
        if (spec.parentTable == table && spec.to == column) pass() else fail("did not find foreign key on '$table'.'$column'")
    }

    return this
}

fun Assertion.Builder<ForeignKeySpec>.onUpdateDoes(action: ForeignKeySpec.Action): Assertion.Builder<ForeignKeySpec> {
    assert("does $action on update") { spec ->
        if (spec.onUpdate == action) pass() else fail("does $action on update")
    }

    return this
}

fun Assertion.Builder<ForeignKeySpec>.onDeleteDoes(action: ForeignKeySpec.Action): Assertion.Builder<ForeignKeySpec> {
    assert("does $action on delete") { spec ->
        if (spec.onDelete == action) pass() else fail("does $action on delete")
    }

    return this
}

fun Handle.tableSpec(tableName: String): TableSpec {
    val columns = select("pragma table_info($tableName)").mapTo<ColumnSpec>().toList()
    val foreignKeys = select("pragma foreign_key_list($tableName)").mapTo<ForeignKeySpec>().toList()

    return TableSpec(columns, foreignKeys)
}