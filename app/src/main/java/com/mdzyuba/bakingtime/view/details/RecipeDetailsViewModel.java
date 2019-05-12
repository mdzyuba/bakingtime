package com.mdzyuba.bakingtime.view.details;

import android.app.Application;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.LoadRecipeTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class RecipeDetailsViewModel extends AndroidViewModel {
    private final MutableLiveData<Recipe> recipe;

    public RecipeDetailsViewModel(@NonNull Application application) {
        super(application);
        recipe = new MutableLiveData<>();
    }

    public void loadRecipe(@NonNull Integer recipeId) {
        LoadRecipeTask loadRecipeTask = new LoadRecipeTask(getApplication(), recipeId, recipe);
        loadRecipeTask.execute();
    }

    public MutableLiveData<Recipe> getRecipe() {
        return recipe;
    }
}
