package com.sorrowblue.kdbc.ktx

import java.sql.ResultSet

inline operator fun <reified T> ResultSet.get(columnName: String): T = this.getObject(columnName, T::class.java)

operator fun ResultSet.iterator(): Iterator<ResultSet> {
	return object : Iterator<ResultSet> {
		override fun hasNext(): Boolean = this@iterator.next()
		override fun next(): ResultSet = this@iterator
	}
}

fun <T> ResultSet.map(fn: (ResultSet) -> T): List<T> = mutableListOf<T>().also { list ->
	use { iterator().forEach { list.add(fn(it)) } }
}
