package com.sorrowblue.kdbc

import java.sql.ResultSet

interface RoomEntity<T, P> {

	val tableName: String

	val isAutoIncrementEnabled: Boolean

	val primaryColumn: String

	val columnNames: List<String>

	val primaryLessColumnNames get() = columnNames.dropWhile { it == primaryColumn }

	fun values(entity: T): List<Any?>

	fun result(rs: ResultSet): T

	fun primaryValue(entity: T): P
}
