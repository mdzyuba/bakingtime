package com.mdzyuba.bakingtime.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mdzyuba.bakingtime.model.Recipe;

import java.lang.reflect.Type;
import java.util.Collection;

public class RecipeFactory {

    public Collection<Recipe> loadRecipes(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Recipe>>(){}.getType();
        return gson.fromJson(json, collectionType);
    }

}
