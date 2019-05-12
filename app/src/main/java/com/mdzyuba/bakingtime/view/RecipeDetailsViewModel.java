package com.mdzyuba.bakingtime.view;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.RecipeFactory;

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
        final RecipeFactory recipeFactory = new RecipeFactory();
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Integer, Void, Recipe> task = new AsyncTask<Integer, Void, Recipe>() {
            @Override
            protected Recipe doInBackground(Integer... args) {
                Integer recipeId = args[0];
                return recipeFactory.loadRecipe(getApplication(), recipeId);
            }

            @Override
            protected void onPostExecute(final Recipe recipe) {
                RecipeDetailsViewModel.this.recipe.postValue(recipe);
            }
        };
        task.execute(recipeId);
    }

    public MutableLiveData<Recipe> getRecipe() {
        return recipe;
    }
}
