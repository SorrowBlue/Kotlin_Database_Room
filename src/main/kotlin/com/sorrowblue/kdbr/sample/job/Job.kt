package com.sorrowblue.kdbr.sample.job

import com.sorrowblue.kdbr.Entity
import com.sorrowblue.kdbr.PrimaryKey

@Entity("jobs")
data class Job(
	@PrimaryKey(true)
	val id: Int,
	val name: String
)
