package com.grd.cookit.repositories;

import android.location.Location;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.model.entities.Recipe;
import com.grd.cookit.model.entities.RecipeFirebase;

import java.io.File;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;

public class RecipeRepository {
    public static final RecipeRepository instance = new RecipeRepository();

    public static void saveRecipe(String recipeName, String description, File image, Location location, OnSuccessListener onSuccessListener) {
        Tasks.call(Executors.newSingleThreadExecutor(), () -> {
            Recipe recipe = new Recipe();
            String uid = UUID.randomUUID().toString();
            recipe.uid = uid;
            recipe.userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            recipe.timestamp = new Date().getTime();
            recipe.name = recipeName;
            recipe.description = description;
            recipe.longitude = location.getLongitude();
            recipe.latitude = location.getLatitude();

            RecipeFirebase.saveImage(image, uid, (newImageUrl) -> {
                recipe.imageUri = newImageUrl.toString();
                RecipeFirebase.saveRecipe(recipe, onSuccessListener);
            });
            return null;
        });

    }
}
