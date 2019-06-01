package com.mdzyuba.bakingtime.view.list;

import android.app.Application;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.LoadRecipeCollectionTask;
import com.mdzyuba.bakingtime.repository.ReloadRecipesTask;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class RecipeListViewModel extends AndroidViewModel {

    private final MutableLiveData<Collection<Recipe>> recipes;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipes = new MutableLiveData<>();
        loadRecipes();
    }

    public void loadRecipes() {
        LoadRecipeCollectionTask task = new LoadRecipeCollectionTask(getApplication(), recipes);
        task.execute();
    }

    public void reloadRecipes() {
        clearRecipes();
        ReloadRecipesTask task = new ReloadRecipesTask(getApplication(), recipes);
        task.execute();
    }

    private void clearRecipes() {
        recipes.postValue(new ArrayList<>());
    }

    public MutableLiveData<Collection<Recipe>> getRecipes() {
        return recipes;
    }
}
