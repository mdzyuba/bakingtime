package com.mdzyuba.bakingtime.view.details;

import com.mdzyuba.bakingtime.model.Step;

import androidx.annotation.NonNull;

public interface RecipeStepSelectorListener {
    void onStepSelected(@NonNull Step step);
}
