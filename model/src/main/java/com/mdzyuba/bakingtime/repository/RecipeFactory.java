package com.mdzyuba.bakingtime.repository;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mdzyuba.bakingtime.db.IngredientDao;
import com.mdzyuba.bakingtime.db.RecipeDao;
import com.mdzyuba.bakingtime.db.RecipeDatabase;
import com.mdzyuba.bakingtime.db.StepDao;
import com.mdzyuba.bakingtime.http.HttpClientProvider;
import com.mdzyuba.bakingtime.model.Ingredient;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class RecipeFactory {

    private final RecipeDatabase database;

    public RecipeFactory(Context context) {
        database = RecipeDatabase.getInstance(context);
    }

    @VisibleForTesting
    Collection<Recipe> loadRecipes(@Nullable String json) {
        if (json == null) {
            Timber.e("Unable to retrieve recipe data from the service");
            // TODO: address the no network case
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Recipe>>(){}.getType();
        Collection<Recipe> recipes = gson.fromJson(json, collectionType);
        updateChildParentReferences(recipes);
        saveRecipesToDb(recipes);
        return recipes;
    }

    private void saveRecipesToDb(Collection<Recipe> recipes) {
        RecipeDao recipeDao = database.recipeDao();
        IngredientDao ingredientDao = database.ingredientDao();
        StepDao stepDao = database.stepDao();
        for (Recipe recipe: recipes) {
            recipeDao.insert(recipe);
            for (Step step: recipe.getSteps()) {
                stepDao.insert(step);
            }
            for (Ingredient ingredient: recipe.getIngredients()) {
                ingredientDao.insert(ingredient);
            }
        }
    }

    public void cleanDb() {
        database.beginTransaction();
        database.clearAllTables();
        database.endTransaction();
    }

    public List<Recipe> loadAllRecipesFromDb() {
        RecipeDao recipeDao = database.recipeDao();
        return recipeDao.loadRecipes();
    }

    public Recipe loadRecipe(@NonNull Integer recipeId) {
        Timber.d("loadRecipe %d", recipeId);
        RecipeDao recipeDao = database.recipeDao();
        Recipe recipe = recipeDao.loadRecipe(recipeId);

        StepDao stepDao = database.stepDao();
        if (recipe == null) {
            return null;
        }
        List<Step> steps = stepDao.loadSteps(recipe.getId());
        recipe.setSteps(steps);

        IngredientDao ingredientDao = database.ingredientDao();
        List<Ingredient> ingredients = ingredientDao.loadIngredients(recipe.getId());
        recipe.setIngredients(ingredients);

        return recipe;
    }

    private void updateChildParentReferences(Collection<Recipe> recipes) {
        for (Recipe recipe: recipes) {
            for (Step step: recipe.getSteps()) {
                step.setRecipeId(recipe.getId());
            }
            int i = 1;
            for (Ingredient ingredient: recipe.getIngredients()) {
                ingredient.setId(i++);
                ingredient.setRecipeId(recipe.getId());
            }
        }
    }

    public Collection<Recipe> loadRecipes(Context context) throws IOException {
        URL url = getUrl();
        return loadRecipes(context, url);
    }

    private URL getUrl() throws MalformedURLException {
        Uri uri = Uri.parse(Config.getRecipeUrl()).buildUpon().build();
        return new URL(uri.toString());
    }

    private Collection<Recipe> loadRecipes(Context context, URL url) throws IOException {
        Timber.d("loadRecipes");
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = HttpClientProvider.getClient(context);
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(body.byteStream(), StandardCharsets.UTF_8))) {
                Gson gson = new GsonBuilder().create();
                Type collectionType = new TypeToken<Collection<Recipe>>(){}.getType();
                Collection<Recipe> recipes = gson.fromJson(reader, collectionType);
                updateChildParentReferences(recipes);
                saveRecipesToDb(recipes);
                return recipes;
            }
        }
    }

}
