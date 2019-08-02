package com.grd.cookit.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.grd.cookit.model.daos.RecipesDao;
import com.grd.cookit.model.daos.UsersDao;
import com.grd.cookit.model.entities.Recipe;
import com.grd.cookit.model.entities.User;

@Database(entities = {User.class, Recipe.class}, version = 2)
public abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UsersDao usersDao();

    public abstract RecipesDao recipesDao();
}
