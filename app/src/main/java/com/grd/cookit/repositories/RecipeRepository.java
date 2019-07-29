package com.grd.cookit.repositories;

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

    public static void saveRecipe(String recipeName, File image, OnSuccessListener onSuccessListener) {
        Tasks.call(Executors.newSingleThreadExecutor(),()->{
        Recipe recipe = new Recipe();
            String uid = UUID.randomUUID().toString();
            recipe.setUid(uid);
            recipe.setUserUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            recipe.setTimestamp(new Date().getTime());
            recipe.setName(recipeName);

            RecipeFirebase.saveImage(image,uid,(newImageUrl)->{
                recipe.setImageUri(newImageUrl.toString());
                RecipeFirebase.saveRecipe(recipe,onSuccessListener);
            });

//            final TaskCompletionSource<Uri> source = new TaskCompletionSource<>();
//            RecipeFirebase.saveImage(image, uid, (newImageUrl) -> source.setResult(newImageUrl));
//            Task<Uri> task = source.getTask();
//            task.addOnCompleteListener(Executors.newSingleThreadExecutor(), (newImageUrl) -> {
//                Uri imageUrl = newImageUrl.getResult();
//                recipe.setImageUri(imageUrl.toString());
//                RecipeFirebase.saveRecipe(recipe,onSuccessListener);
//            });
            return null;
        });

    }
}
