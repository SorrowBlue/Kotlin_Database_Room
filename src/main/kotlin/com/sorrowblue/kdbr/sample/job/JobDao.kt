package com.sorrowblue.kdbr.sample.job

import com.sorrowblue.kdbr.Dao
import com.sorrowblue.kdbr.Query

@Dao
interface JobDao {

	@Query("SELECT * FROM jobs")
	fun getAll(): List<Job>
}
