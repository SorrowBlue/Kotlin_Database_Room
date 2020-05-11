package com.sorrowblue.kdbc.sample

import com.sorrowblue.kdbc.Database
import com.sorrowblue.kdbc.Room
import com.sorrowblue.kdbc.RoomDatabase
import com.sorrowblue.kdbc.sample.job.JobDao

@Database
abstract class AppDatabase : RoomDatabase {

	override val url: String = "jdbc:postgresql://localhost:5432/training"
	override val user: String = "training"
	override val password: String = "training"

	abstract fun userDao(): UserDao
	abstract fun jobDao(): JobDao
}

fun main() {
	val database: AppDatabase = Room.databaseBuilder(AppDatabase::class.java)
	println(database.jobDao().getAll())
}


