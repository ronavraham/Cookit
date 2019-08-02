package com.grd.cookit.model.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.grd.cookit.model.entities.Recipe;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipesDao {
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllPosts();

    @Query("SELECT * FROM Recipe WHERE Uid = :uid")
    Recipe GetPostByUid(String uid);

    @Insert(onConflict = REPLACE)
    void insertPost(Recipe post);

    @Insert(onConflict = REPLACE)
    void insertAllPosts(Recipe... posts);

    @Query("DELETE FROM Recipe WHERE Uid = :uid")
    void deletePost(String uid);
}
