package com.mdzyuba.bakingtime.model;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Recipe {

    private int id;
    @Nullable
    private String name;
    @NonNull
    private List<Ingredient> ingredients;
    @NonNull
    private List<Step> steps;
    private int servings;
    // optional
    @Nullable
    private String image;

    public Recipe(int id, @Nullable String name, @NonNull List<Ingredient> ingredients,
                  @NonNull List<Step> steps, int servings, @Nullable String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @NonNull
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @NonNull
    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Recipe{" + "id=" + id + ", name='" + name + '\'' + ", ingredients=" + ingredients +
               ", steps=" + steps + ", servings=" + servings + ", image='" + image + '\'' + '}';
    }
}
