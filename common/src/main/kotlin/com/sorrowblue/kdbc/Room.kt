package com.sorrowblue.kdbc

object Room {
	@Suppress("UNCHECKED_CAST")
	fun <T, E : RoomDatabase> databaseBuilder(klass: Class<E>): T {
		val c = Class.forName("${klass.`package`.name}.room.${klass.simpleName}_Imp")
		return c.getDeclaredConstructor().newInstance() as T
	}
}
