package com.sorrowblue.kdbr.sample

import com.sorrowblue.kdbr.Entity
import com.sorrowblue.kdbr.PrimaryKey

@Entity("users")
data class User(
	@PrimaryKey()
	val id: Int,
	val password: String,
	val level: Int
)
