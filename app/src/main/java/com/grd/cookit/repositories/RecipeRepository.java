package com.grd.cookit.repositories;

import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.model.AppLocalDb;
import com.grd.cookit.model.entities.Recipe;
import com.grd.cookit.model.entities.User;
import com.grd.cookit.model.firebase.RecipeFirebase;
import com.grd.cookit.model.ui.UIRecipe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RecipeRepository {
    public static final RecipeRepository instance = new RecipeRepository();
    public MutableLiveData<List<UIRecipe>> recipes;

    RecipeRepository() {
        recipes = new MutableLiveData<>();
    }

    public LiveData<List<UIRecipe>> getAllRecipes() {
        synchronized (this) {
            AsyncTask task = new GetAllRecipesTask();
            task.execute(new String[]{});
        }

        return this.recipes;
    }

    public static void saveRecipe(String recipeName,
                                  String description,
                                  File image,
                                  Location location,
                                  OnSuccessListener onSuccessListener,
                                  OnFailureListener onFailureListener) {
        Tasks.call(Executors.newSingleThreadExecutor(), () -> {
            Recipe recipe = new Recipe();
            String uid = UUID.randomUUID().toString();
            recipe.uid = uid;
            recipe.userGoogleUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            recipe.timestamp = new Date().getTime();
            recipe.name = recipeName;
            recipe.description = description;
            recipe.longitude = location.getLongitude();
            recipe.latitude = location.getLatitude();

            RecipeFirebase.saveImage(image, uid, (newImageUrl) -> {
                recipe.imageUri = newImageUrl.toString();
                RecipeFirebase.saveRecipe(recipe, onSuccessListener,onFailureListener);
            },onFailureListener);
            return null;
        });

    }

    private List<UIRecipe> makePostsForList(List<Recipe> recipes, List<User> users) {
        List<UIRecipe> result = new ArrayList<>();

        List<UIRecipe> finalResult = result;

        recipes.stream().forEach((recipe) -> {
            Optional<User> optUser = users.stream().filter(
                    (user1) -> user1.getGoogleUid().equals(recipe.userGoogleUid))
                    .findAny();

            if (!optUser.isPresent()) {
                return;
            }

            User user = optUser.get();

            UIRecipe uiRecipe = new UIRecipe();
            uiRecipe.uid = recipe.uid;
            uiRecipe.userName = user.getName();
            uiRecipe.timestamp = new Date(recipe.timestamp);
            uiRecipe.latitude = recipe.latitude;
            uiRecipe.longitude = recipe.longitude;
            try {
                uiRecipe.imagine = new BitmapDrawable(Picasso.get().load(recipe.imageUri).get());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                uiRecipe.userProfileImage = new BitmapDrawable(Picasso.get().load(user.getProfileUri()).get());
            } catch (IOException e) {
                e.printStackTrace();
            }

            finalResult.add(uiRecipe);
        });

        result = finalResult.stream().sorted(Comparator.comparing(UIRecipe::getTimestamp).reversed()).collect(Collectors.toList());
        return result;
    }

    private class GetAllRecipesTask extends AsyncTask<String, String, List<UIRecipe>> {

        @Override
        protected List<UIRecipe> doInBackground(String... strings) {
            final TaskCompletionSource<Pair<List<Recipe>, List<User>>> source = new TaskCompletionSource<>();

            RecipeFirebase.getInstance().getAllCollections((pair) -> {
                Tasks.call(Executors.newSingleThreadExecutor(), () -> {
                    Pair<List<Recipe>, List<User>> tuple = (Pair<List<Recipe>, List<User>>) pair;
                    List<Recipe> posts = tuple.first;
                    List<User> users = tuple.second;
                    List<UIRecipe> result = makePostsForList(posts, users);
                    recipes.postValue(result);

                    AppLocalDb.db.usersDao().insertAllUsers(users.toArray(new User[0]));
                    AppLocalDb.db.recipesDao().insertAllPosts(posts.toArray(new Recipe[0]));
                    return true;
                });
            });

            List<Recipe> postsFromDb = AppLocalDb.db.recipesDao().getAllPosts();
            List<User> usersFromDb = AppLocalDb.db.usersDao().getAllUsers();

            return null;
        }
    }

}
