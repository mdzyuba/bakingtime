package com.mdzyuba.bakingtime.view.ingredients;

import android.app.Application;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.LoadRecipeTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class IngredientsListViewModel extends AndroidViewModel {
    private final MutableLiveData<Recipe> recipe;

    public IngredientsListViewModel(@NonNull Application application) {
        super(application);
        recipe = new MutableLiveData<>();
    }

    public void loadRecipe(@NonNull Integer recipeId) {
        Timber.d("Load recipe: %d", recipeId);
        LoadRecipeTask loadRecipeTask = new LoadRecipeTask(getApplication(), recipeId, recipe);
        loadRecipeTask.execute();
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }
}
