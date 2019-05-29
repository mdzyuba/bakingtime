package com.mdzyuba.testutil;

import android.content.Context;

import com.mdzyuba.bakingtime.db.RecipeDatabase;
import com.mdzyuba.bakingtime.repository.Config;

import androidx.room.Room;

public class TestConfig {
    private static final String TEST_DATABASE_NAME = "test_recipes";

    public static void setupTestDb(Context context, String recipeUrl) {
        Config.setRecipeUrl(recipeUrl);
        RecipeDatabase database = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                                                               RecipeDatabase.class)
                                      .allowMainThreadQueries()
                                      .build();
        RecipeDatabase.setInstance(database);
    }

    public static void setupInstrumentationTestDb(Context context, String recipeUrl) {
        Config.setRecipeUrl(recipeUrl);
        RecipeDatabase database = Room.databaseBuilder(context.getApplicationContext(),
                                     RecipeDatabase.class,
                                     TEST_DATABASE_NAME)
                                      .allowMainThreadQueries()
                                      .build();
        RecipeDatabase.setInstance(database);
    }

}
