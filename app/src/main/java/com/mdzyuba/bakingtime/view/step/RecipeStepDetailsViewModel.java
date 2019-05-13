package com.mdzyuba.bakingtime.view.step;

import android.app.Application;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.repository.LoadRecipeTask;
import com.mdzyuba.bakingtime.repository.LoadStepTask;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class RecipeStepDetailsViewModel extends AndroidViewModel {

    public final MutableLiveData<Step> step;
    public final MutableLiveData<Recipe> recipe;
    public final ObservableBoolean nextStepAvailable;
    public final ObservableBoolean previousStepAvailable;

    private int stepIndex;
    private int totalSteps;

    public RecipeStepDetailsViewModel(@NonNull Application application) {
        super(application);
        step = new MutableLiveData<>();
        recipe = new MutableLiveData<>();
        nextStepAvailable = new ObservableBoolean(false);
        previousStepAvailable = new ObservableBoolean(false);
    }

    public void loadStep(int stepId) {
        LoadStepTask loadStepTask = new LoadStepTask(getApplication(), stepId, step);
        loadStepTask.execute();
        step.observeForever(new Observer<Step>() {
            @Override
            public void onChanged(Step step) {
                RecipeStepDetailsViewModel.this.step.removeObserver(this);
                LoadRecipeTask loadRecipeTask = new LoadRecipeTask(getApplication(), step.getRecipeId(), recipe);
                loadRecipeTask.execute();
            }
        });
        recipe.observeForever(new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                RecipeStepDetailsViewModel.this.recipe.removeObserver(this);
                Step step = RecipeStepDetailsViewModel.this.step.getValue();
                if (step == null) {
                    return;
                }
                List<Step> steps = recipe.getSteps();
                stepIndex = steps.indexOf(step);
                totalSteps = steps.size();
                nextStepAvailable.set(stepIndex < totalSteps - 1);
                previousStepAvailable.set(stepIndex > 0);
            }
        });
    }

    @Nullable
    public Step getNextStep() {
        if (recipe.getValue() == null) {
            return null;
        }

        if (stepIndex < totalSteps - 1) {
            return recipe.getValue().getSteps().get(stepIndex + 1);
        }
        return null;
    }

    @Nullable
    public Step getPrevStep() {
        if (recipe.getValue() == null) {
            return null;
        }
        if (stepIndex > 0) {
            return recipe.getValue().getSteps().get(stepIndex - 1);
        }
        return null;
    }

}
