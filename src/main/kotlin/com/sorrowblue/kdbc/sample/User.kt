package com.sorrowblue.kdbc.sample

import com.sorrowblue.kdbc.Entity
import com.sorrowblue.kdbc.PrimaryKey

@Entity("users")
data class User(
	@PrimaryKey()
	val id: Int,
	val password: String,
	val level: Int
)
