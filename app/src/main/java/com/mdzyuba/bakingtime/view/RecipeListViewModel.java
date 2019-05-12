package com.mdzyuba.bakingtime.view;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.RecipeFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class RecipeListViewModel extends AndroidViewModel {

    private final MutableLiveData<Collection<Recipe>> recipes;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipes = new MutableLiveData<>();
        loadRecipes();
    }

    private void loadRecipes() {
        LoadRecipesTask task = new LoadRecipesTask(getApplication(), recipes);
        task.execute();
    }

    private static class LoadRecipesTask extends AsyncTask<Void, Void, Collection<Recipe>> {
        private final MutableLiveData<Collection<Recipe>> recipes;
        private final WeakReference<Context> contextWeakReference;

        LoadRecipesTask(Context context, MutableLiveData<Collection<Recipe>> recipes) {
            this.contextWeakReference = new WeakReference<>(context);
            this.recipes = recipes;
        }

        @Override
        protected Collection<Recipe> doInBackground(Void... voids) {
            try {
                RecipeFactory factory = new RecipeFactory();
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

    public MutableLiveData<Collection<Recipe>> getRecipes() {
        return recipes;
    }
}
