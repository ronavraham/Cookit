package com.grd.cookit.model.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.grd.cookit.model.entities.User;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UsersDao {
    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM User WHERE googleUid = :uid")
    User getUserByUid(String uid);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Delete
    void deleteUser(User user);

    @Insert(onConflict = REPLACE)
    void insertAllUsers(User... u);
}
