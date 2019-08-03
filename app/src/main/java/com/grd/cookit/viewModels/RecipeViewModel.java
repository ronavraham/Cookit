package com.grd.cookit.viewModels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.repositories.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends ViewModel {
    public static final RecipeViewModel instance = new RecipeViewModel();

    public LiveData<List<UIRecipe>> recipes;
    public MutableLiveData<UIRecipe> selectedRecipe;
    public LiveData<List<UIRecipe>> recipesForProfile;
    private boolean isRecipesBinded = false;
    private boolean isProfileBinded = false;
    public MutableLiveData<Boolean> profileBusy;
    public MutableLiveData<Boolean> feedBusy;

    private RecipeViewModel() {
        super();
        profileBusy = new MutableLiveData<>();
        feedBusy = new MutableLiveData<>();
        selectedRecipe = new MutableLiveData<>();

        profileBusy.setValue(false);
        feedBusy.setValue(false);
    }

    public LiveData<List<UIRecipe>> getAllRecipes() {
        if (!isRecipesBinded) {
            isRecipesBinded = true;
            feedBusy.setValue(true);
            recipes = RecipeRepository.instance.getAllRecipes();
        }
        return recipes;
    }

    public LiveData<List<UIRecipe>> getAllProfilePosts(String uid) {
        if (!isProfileBinded) {
            isProfileBinded = true;
            profileBusy.setValue(true);
            recipesForProfile = RecipeRepository.instance.getAllRecipesForProfile(uid);
        }
        return recipesForProfile;
    }


    public void deletePost(String postUid) {
        RecipeRepository.instance.deleteRecipe(postUid);
        updateBusy();
    }

    public MutableLiveData<UIRecipe> getselectedRecipe(){
        return this.selectedRecipe;
    }

    public void selectRecipe(UIRecipe recipe){
        this.selectedRecipe.setValue(recipe);
    }

    private void updateBusy() {
        profileBusy.setValue(true);
        feedBusy.setValue(true);
    }

}
