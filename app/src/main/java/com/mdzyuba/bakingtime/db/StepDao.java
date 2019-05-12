package com.mdzyuba.bakingtime.db;

import com.mdzyuba.bakingtime.model.Step;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface StepDao {

    @Query("SELECT * from step WHERE recipeId = :recipeId ORDER BY id")
    List<Step> loadSteps(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Step step);

    @Update
    void update(Step step);

    @Delete
    void delete(Step step);

    @Query("DELETE FROM step")
    void deleteAll();
}
