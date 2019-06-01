package com.mdzyuba.bakingtime.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.mdzyuba.bakingtime.model.Recipe;

import java.lang.ref.WeakReference;

import androidx.lifecycle.MutableLiveData;

/**
 * Loads a recipe from the DB.
 */
public class LoadRecipeTask extends AsyncTask<Integer, Void, Recipe> {
    private final WeakReference<Context> contextWeakReference;
    private final Integer recipeId;
    private final MutableLiveData<Recipe> recipe;

    public LoadRecipeTask(Context context,
                          Integer recipeId,
                          MutableLiveData<Recipe> recipe) {
        this.contextWeakReference =  new WeakReference<>(context);
        this.recipeId = recipeId;
        this.recipe = recipe;
    }

    @Override
    protected Recipe doInBackground(Integer... args) {
        Context context = contextWeakReference.get();
        if (context == null) {
            return null;
        }
        RecipeFactory recipeFactory = new RecipeFactory(context);
        return recipeFactory.loadRecipe(recipeId);
    }

    @Override
    protected void onPostExecute(final Recipe recipe) {
        if (recipe != null) {
            this.recipe.postValue(recipe);
        }
    }
}
