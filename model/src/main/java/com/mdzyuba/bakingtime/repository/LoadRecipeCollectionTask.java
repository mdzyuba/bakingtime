package com.mdzyuba.bakingtime.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.mdzyuba.bakingtime.model.Recipe;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;

import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class LoadRecipeCollectionTask extends AsyncTask<Void, Void, Collection<Recipe>> {
    private final MutableLiveData<Collection<Recipe>> recipes;
    private final WeakReference<Context> contextWeakReference;

    public LoadRecipeCollectionTask(Context context, MutableLiveData<Collection<Recipe>> recipes) {
        this.contextWeakReference = new WeakReference<>(context);
        this.recipes = recipes;
    }

    @Override
    protected Collection<Recipe> doInBackground(Void... voids) {
        try {
            Context context = contextWeakReference.get();
            if (context == null) {
                return null;
            }
            RecipeFactory factory = new RecipeFactory(context);
            if (contextWeakReference.get() != null) {
                return factory.loadRecipes(contextWeakReference.get());
            }
        } catch (IOException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Collection<Recipe> recipes) {
        this.recipes.postValue(recipes);
    }
}