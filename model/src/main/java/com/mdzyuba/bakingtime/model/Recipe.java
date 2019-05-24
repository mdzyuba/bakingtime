package com.mdzyuba.bakingtime.model;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe",
        indices = { @Index(value = {"id"}, unique = true)})
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @Nullable
    private String name;

    @Ignore
    private List<Ingredient> ingredients;

    @Ignore
    private List<Step> steps;

    private int servings;

    // optional
    @Nullable
    private String image;

    public Recipe() {
        id = 0;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(@NonNull List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(@NonNull List<Step> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Recipe{" + "id=" + id + ", name='" + name + '\'' + ", ingredients=" + ingredients +
               ", steps=" + steps + ", servings=" + servings + ", image='" + image + '\'' + '}';
    }

}
