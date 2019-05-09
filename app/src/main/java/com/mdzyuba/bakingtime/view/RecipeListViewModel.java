package com.mdzyuba.bakingtime.view;

import android.app.Application;
import android.os.AsyncTask;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.RecipeFactory;

import java.io.IOException;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class RecipeListViewModel extends AndroidViewModel {

    private MutableLiveData<Collection<Recipe>> recipes;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipes = new MutableLiveData<>();
        loadRecipes();
    }

    private void loadRecipes() {
        AsyncTask<Void, Void, Collection<Recipe>> task = new AsyncTask<Void, Void, Collection<Recipe>>() {
            @Override
            protected Collection<Recipe> doInBackground(Void... voids) {
                try {
                    RecipeFactory factory = new RecipeFactory();
                    Collection<Recipe> recipeCollection = factory.loadRecipes(getApplication());
                    return recipeCollection;
                } catch (IOException e) {
                    Timber.e(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Collection<Recipe> recipes) {
                RecipeListViewModel.this.recipes.postValue(recipes);

            }
        };
        task.execute();

    }

    public MutableLiveData<Collection<Recipe>> getRecipes() {
        return recipes;
    }
}
