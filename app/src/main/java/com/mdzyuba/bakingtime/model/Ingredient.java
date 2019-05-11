package com.mdzyuba.bakingtime.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Ingredient implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.quantity);
        dest.writeString(this.measure);
        dest.writeString(this.ingredient);
    }

    protected Ingredient(Parcel in) {
        this.quantity = in.readFloat();
        this.measure = in.readString();
        this.ingredient = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
