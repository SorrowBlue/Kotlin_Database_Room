package com.sorrowblue.kdbr

object Room {
	@Suppress("UNCHECKED_CAST")
	fun <E : RoomDatabase> databaseBuilder(clazz: Class<E>): E =
		Class.forName("${clazz.`package`.name}.room.${clazz.simpleName}_Imp")
			.getDeclaredConstructor().newInstance() as E
}
