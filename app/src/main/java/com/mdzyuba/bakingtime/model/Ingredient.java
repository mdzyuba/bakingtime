package com.mdzyuba.bakingtime.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredient",
        foreignKeys = @ForeignKey(entity = Recipe.class,
                                  parentColumns = "id",
                                  childColumns = "recipeId",
                                  onDelete = ForeignKey.CASCADE),
        indices = { @Index( value = {"recipeId", "ingredient"}, unique = true)})
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    private Integer pk;

    /**
     * Used to keep the same order of ingredients as provided from the Web.
     */
    private Integer id;

    @NonNull
    private String ingredient;

    @NonNull
    private Integer recipeId;

    private float quantity;

    @Nullable
    private String measure;

    public Ingredient(@NonNull String ingredient, @NonNull Integer recipeId, float quantity,
                      @Nullable String measure) {
        this.ingredient = ingredient;
        this.recipeId = recipeId;
        this.quantity = quantity;
        this.measure = measure;
    }

    public float getQuantity() {
        return quantity;
    }

    @Nullable
    public String getMeasure() {
        return measure;
    }

    @NonNull
    public String getIngredient() {
        return ingredient;
    }

    @NonNull
    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(@NonNull Integer recipeId) {
        this.recipeId = recipeId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ingredient{" + "quantity=" + quantity + ", measure='" + measure + '\'' +
               ", ingredient='" + ingredient + '\'' + '}';
    }

}
