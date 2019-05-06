package com.mdzyuba.bakingtime.model;

import androidx.annotation.Nullable;

public class Ingredient {

    private float quantity;
    // TODO: convert to an enum
    @Nullable
    private String measure;
    @Nullable
    private String ingredient;

    public Ingredient(float quantity, @Nullable String measure, @Nullable String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public float getQuantity() {
        return quantity;
    }

    @Nullable
    public String getMeasure() {
        return measure;
    }

    @Nullable
    public String getIngredient() {
        return ingredient;
    }

    @Override
    public String toString() {
        return "Ingredient{" + "quantity=" + quantity + ", measure='" + measure + '\'' +
               ", ingredient='" + ingredient + '\'' + '}';
    }
}
