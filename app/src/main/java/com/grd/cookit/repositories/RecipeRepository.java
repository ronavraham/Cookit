package com.grd.cookit.repositories;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.model.entities.Recipe;
import com.grd.cookit.model.entities.RecipeFirebase;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeRepository {
    public static final RecipeRepository instance = new RecipeRepository();

    public static void saveRecipe(String recipeName, Bitmap image) {
        Tasks.call(Executors.newSingleThreadExecutor(),()->{
        Recipe recipe = new Recipe();
            String uid = UUID.randomUUID().toString();
            recipe.setUid(uid);
            recipe.setUserUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            recipe.setTimestamp(new Date().getTime());
            recipe.setName(recipeName);

            final TaskCompletionSource<Uri> source = new TaskCompletionSource<>();
            RecipeFirebase.saveImage(image, uid, (newImageUrl) -> source.setResult(newImageUrl));
            Task<Uri> task = source.getTask();
            task.addOnCompleteListener(Executors.newSingleThreadExecutor(), (newImageUrl) -> {
                Uri imageUrl = newImageUrl.getResult();
                recipe.setImageUri(imageUrl.toString());
                RecipeFirebase.saveRecipe(recipe, o -> {
                });
            });
            return null;
        });

    }
}
