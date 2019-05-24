package com.mdzyuba.bakingtime.db;

import android.content.Context;

import com.mdzyuba.bakingtime.model.Ingredient;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import timber.log.Timber;


@Database(entities = {
        Recipe.class,
        Ingredient.class,
        Step.class
}, version = 1, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "recipes";
    private static final Object LOCK = new Object();
    private static RecipeDatabase sInstance;

    public static RecipeDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Timber.d("Creating a new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                                 RecipeDatabase.class,
                                                 RecipeDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract StepDao stepDao();

}
