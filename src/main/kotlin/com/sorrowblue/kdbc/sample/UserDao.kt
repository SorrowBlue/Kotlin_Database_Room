package com.sorrowblue.kdbc.sample

import com.sorrowblue.kdbc.*

@Dao
interface UserDao {

	@Query("SELECT * FROM users;")
	fun getAll(): List<User>

	@Query("SELECT * FROM users WHERE id = :id;")
	fun get(id: Int): User?

	@Delete
	fun delete(user: User): Int

	@Delete
	fun deleteUnit(user: User)

	@Update
	fun update(user: User): Int

	@Update
	fun updateUnit(user: User)

	@Insert
	fun insert(user: User): Int

	@Insert
	fun insertAll(users: List<User>)

	@Insert
	fun inserts(vararg users: User)
}
