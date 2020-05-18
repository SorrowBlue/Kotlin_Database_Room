package com.sorrowblue.kdbr.sample

import com.sorrowblue.kdbr.Database
import com.sorrowblue.kdbr.Room
import com.sorrowblue.kdbr.RoomDatabase
import com.sorrowblue.kdbr.sample.job.Job
import com.sorrowblue.kdbr.sample.job.JobDao

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
//	println(database.jobDao().add(Job(14, "tesuto")))
//	println(database.jobDao().getAll())
//	println(database.jobDao().delete(Job(14, "tesuto")))
//	println(database.jobDao().getAll())
}


