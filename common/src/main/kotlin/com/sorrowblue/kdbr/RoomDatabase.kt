package com.sorrowblue.kdbr

import java.sql.Connection

interface RoomDatabase {
	val url: String
	val user: String
	val password: String
	val connection: Connection
}
