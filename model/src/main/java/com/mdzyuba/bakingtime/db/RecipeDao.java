package com.mdzyuba.bakingtime.db;

import com.mdzyuba.bakingtime.model.Recipe;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecipeDao {

    @Query("SELECT * from recipe ORDER BY id")
    List<Recipe> loadRecipes();

    @Query("SELECT * from recipe WHERE id = :id")
    Recipe loadRecipe(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("DELETE FROM recipe")
    void deleteAll();
}
