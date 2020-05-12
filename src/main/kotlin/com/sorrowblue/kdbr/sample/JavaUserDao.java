package com.sorrowblue.kdbr.sample;

import com.sorrowblue.kdbr.Dao;
import com.sorrowblue.kdbr.Insert;

@Dao
public interface JavaUserDao {

	@Insert
	void inserts(User... users);

}
