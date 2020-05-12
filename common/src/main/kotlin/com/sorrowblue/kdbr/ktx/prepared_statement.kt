package com.sorrowblue.kdbr.ktx

import java.sql.PreparedStatement
import java.sql.ResultSet

fun PreparedStatement.update(): Int = use { executeUpdate() }

fun <T> PreparedStatement.query(block: (ResultSet) -> T): T = use { block(executeQuery()) }

fun PreparedStatement.setAll(vararg value: Any?) {
	this.clearParameters()
	value.forEachIndexed { index, any -> setObject(index + 1, any) }
}
