package com.sorrowblue.kdbc.sample.job

import com.sorrowblue.kdbc.Entity
import com.sorrowblue.kdbc.PrimaryKey

@Entity("jobs")
data class Job(
	@PrimaryKey(true)
	val id: Int,
	val name: String
)
