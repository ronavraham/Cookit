package com.grd.cookit.model.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.grd.cookit.model.entities.Recipe;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipesDao {
    @Query("SELECT * FROM Recipe ORDER BY timestamp DESC")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM Recipe WHERE Uid = :uid ORDER BY timestamp DESC")
    Recipe getRecipeById(String uid);

    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe post);

    @Insert(onConflict = REPLACE)
    void insertAllRecipes(Recipe... posts);

    @Query("DELETE FROM Recipe")
    void deleteAllRecipes();

    @Query("DELETE FROM Recipe WHERE Uid = :uid")
    void deleteRecipe(String uid);
}
