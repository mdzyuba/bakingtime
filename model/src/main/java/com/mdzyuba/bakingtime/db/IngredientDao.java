package com.mdzyuba.bakingtime.db;

import com.mdzyuba.bakingtime.model.Ingredient;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface IngredientDao {

    @Query("SELECT * from ingredient WHERE recipeId = :recipeId ORDER BY id")
    List<Ingredient> loadIngredients(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Delete
    void delete(Ingredient ingredient);

    @Query("DELETE FROM ingredient")
    void deleteAll();
}
