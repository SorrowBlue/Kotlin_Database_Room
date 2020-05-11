package com.sorrowblue.kdbc.sample;

import com.sorrowblue.kdbc.Dao;
import com.sorrowblue.kdbc.Insert;

@Dao
public interface JavaUserDao {

	@Insert
	void inserts(User... users);

}
