package com.sorrowblue.kdbr.sample.job

import com.sorrowblue.kdbr.Dao
import com.sorrowblue.kdbr.Query

@Dao
interface JobDao {

	@Query("SELECT * FROM jobs")
	fun getAll(): List<Job>

	@Query("SELECT * FROM jobs WHERE id = :id")
	fun get(id: Int): List<Job>

	@Query("SELECT * FROM jobs WHERE name = :name")
	fun find(name: String): List<Job>
}
