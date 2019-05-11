package com.mdzyuba.bakingtime.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Recipe implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.ingredients);
        dest.writeTypedList(this.steps);
        dest.writeInt(this.servings);
        dest.writeString(this.image);
    }

    protected Recipe(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        this.steps = in.createTypedArrayList(Step.CREATOR);
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
