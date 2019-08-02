package com.grd.cookit.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.repositories.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends ViewModel {
    public static final RecipeViewModel instance = new RecipeViewModel();

    public LiveData<List<UIRecipe>> recipes;
    private boolean isRecipesBinded = false;
    public MutableLiveData<Boolean> profileBusy;
    public MutableLiveData<Boolean> feedBusy;

    private RecipeViewModel() {
        super();
        feedBusy = new MutableLiveData<>();
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

}
