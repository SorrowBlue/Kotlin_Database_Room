package com.sorrowblue.kdbc.sample

import com.sorrowblue.kdbc.ColumnInfo
import com.sorrowblue.kdbc.Entity
import com.sorrowblue.kdbc.PrimaryKey
import java.time.LocalDate

@Entity("users")
data class User(
	@PrimaryKey(true)
	val id: Int,
	val name: String,
	val age: Int,
	val birthday: LocalDate,

	@ColumnInfo("phone_number")
	val phoneNumber: String
)
