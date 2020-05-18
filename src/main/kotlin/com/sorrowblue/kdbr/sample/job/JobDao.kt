package com.sorrowblue.kdbr.sample.job

import com.sorrowblue.kdbr.*
import java.sql.ResultSet

@Dao
interface JobDao {

	@Query("SELECT * FROM jobs")
	fun getAll(): List<Job>

	@Query("SELECT * FROM jobs WHERE id = :id")
	fun get(id: Int): List<Job>

	@Query("SELECT * FROM jobs WHERE name = :name")
	fun find(name: String): List<Job>

	@Query("SELECT count(*) FROM jobs;", true)
	fun count(): List<Any?>

	@Update
	fun update(job: Job): Int

	@Insert
	fun add(job: Job): Int

	@Insert
	fun addAll(vararg job: Job): Int

	@Insert
	fun addAll(jobs: List<Job>): Int

	@Delete
	fun delete(job: Job): Int
}
