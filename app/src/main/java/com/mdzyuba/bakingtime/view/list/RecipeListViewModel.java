package com.mdzyuba.bakingtime.view.list;

import android.app.Application;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.LoadRecipeCollectionTask;

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

    public MutableLiveData<Collection<Recipe>> getRecipes() {
        return recipes;
    }
}
