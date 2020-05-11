package com.sorrowblue.kdbc.sample.job

import com.sorrowblue.kdbc.Dao
import com.sorrowblue.kdbc.Query
import com.sorrowblue.kdbc.RoomDao

@Dao
interface JobDao {

	@Query("SELECT * FROM jobs")
	fun getAll(): List<Job>
}
