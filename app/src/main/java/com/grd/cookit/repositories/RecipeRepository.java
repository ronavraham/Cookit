package com.grd.cookit.repositories;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    public MutableLiveData<List<UIRecipe>> recipesForUser;

    RecipeRepository() {
        recipes = new MutableLiveData<>();
        recipesForUser = new MutableLiveData<>();
    }

    public LiveData<List<UIRecipe>> getAllRecipes() {
        synchronized (this) {
            AsyncTask task = new GetAllRecipesTask();
            task.execute(new String[]{});
        }

        return this.recipes;
    }

    public LiveData<List<UIRecipe>> getAllRecipesForProfile(String uid) {
        synchronized (this) {
            AsyncTask task = new GetAllRecipesForProfileTask();
            task.execute(new String[]{uid});
        }

        return this.recipesForUser;
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
            recipe.setUid(uid);
            recipe.setUserGoogleUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            recipe.setTimestamp(new Date().getTime());
            recipe.setName(recipeName);
            recipe.setDescription(description);
            recipe.setLongitude(location.getLongitude());
            recipe.setLatitude(location.getLatitude());

            RecipeFirebase.saveImage(image, uid, (newImageUrl) -> {
                recipe.setImageUri(newImageUrl.toString());
                RecipeFirebase.saveRecipe(recipe, onSuccessListener, onFailureListener);
            }, onFailureListener);
            return null;
        });

    }

    private List<UIRecipe> makeRecipesForList(List<Recipe> recipes, List<User> users) {
        List<UIRecipe> result = new ArrayList<>();

        List<UIRecipe> finalResult = result;

        recipes.stream().forEach((recipe) -> {
            Optional<User> optUser = users.stream().filter(
                    (user1) -> user1.getGoogleUid().equals(recipe.getUserGoogleUid()))
                    .findAny();

            if (!optUser.isPresent()) {
                return;
            }

            User user = optUser.get();

            UIRecipe uiRecipe = new UIRecipe();
            uiRecipe.uid = recipe.getUid();
            uiRecipe.userName = user.getName();
            uiRecipe.timestamp = new Date(recipe.getTimestamp());
            uiRecipe.latitude = recipe.getLatitude();
            uiRecipe.longitude = recipe.getLongitude();
            try {
                uiRecipe.imagine = new BitmapDrawable(Picasso.get().load(recipe.getImageUri()).get());
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

    private List<UIRecipe> makeRecipesForList(List<Recipe> posts, User user) {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        return makeRecipesForList(posts, userList);
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
                    List<UIRecipe> result = makeRecipesForList(posts, users);
                    recipes.postValue(result);

                    AppLocalDb.db.recipesDao().deleteAllRecipes();
                    AppLocalDb.db.usersDao().deleteAllUsers();

                    AppLocalDb.db.usersDao().insertAllUsers(users.toArray(new User[0]));
                    AppLocalDb.db.recipesDao().insertAllRecipes(posts.toArray(new Recipe[0]));
                    return true;
                });
            });

            List<Recipe> postsFromDb = AppLocalDb.db.recipesDao().getAllRecipes();
            List<User> usersFromDb = AppLocalDb.db.usersDao().getAllUsers();

            recipes.postValue(makeRecipesForList(postsFromDb,usersFromDb));

            return null;
        }
    }

    private class GetAllRecipesForProfileTask extends AsyncTask<String, Void, List<UIRecipe>> {
        @Override
        protected List<UIRecipe> doInBackground(String... uid) {
            final TaskCompletionSource<Pair<List<Recipe>, User>> source = new TaskCompletionSource<>();

            RecipeFirebase.getInstance().getAllCollectionsForProfile(uid[0], (pair) -> {
                Tasks.call(Executors.newSingleThreadExecutor(), () -> {
                    Pair<List<Recipe>, User> tuple = (Pair<List<Recipe>, User>) pair;
                    List<Recipe> posts = tuple.first;
                    User user = tuple.second;
                    List<UIRecipe> result = makeRecipesForList(posts, user);
                    recipesForUser.postValue(result);

                    AppLocalDb.db.usersDao().insertAllUsers(user);
                    AppLocalDb.db.recipesDao().insertAllRecipes(posts.toArray(new Recipe[0]));

                    return true;
                });
            });

            List<Recipe> postsFromDb = AppLocalDb.db.recipesDao().getAllRecipes();
            User usersFromDb = AppLocalDb.db.usersDao().getUserByUid(uid[0]);

            recipesForUser.postValue(makeRecipesForList(postsFromDb, usersFromDb));

            return null;
        }
    }

    public void deleteRecipe(String postUid) {
        Tasks.call(Executors.newSingleThreadExecutor(), () -> {
            RecipeFirebase.getInstance().deletePost(postUid);
            AppLocalDb.db.recipesDao().deleteRecipe(postUid);
            return true;
        });
    }

}
