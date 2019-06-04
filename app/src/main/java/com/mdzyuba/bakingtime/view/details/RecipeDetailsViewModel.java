package com.mdzyuba.bakingtime.view.details;

import android.app.Application;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.repository.LoadRecipeTask;
import com.mdzyuba.bakingtime.view.IntentArgs;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import timber.log.Timber;

public class RecipeDetailsViewModel extends AndroidViewModel {
    public final ObservableBoolean nextStepAvailable;
    public final ObservableBoolean previousStepAvailable;
    public final MutableLiveData<Boolean> ingredientsSelectorLd;

    private final MutableLiveData<Recipe> recipe;
    // Current step
    private final MutableLiveData<Step> step;
    private final MutableLiveData<Integer> stepIndexLd;

    private final Observer<Integer> stepIndexObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer stepIndex) {
            if (IntentArgs.STEP_NOT_SELECTED == stepIndex) {
                return;
            }
            // Either a step or an ingredients item could be selected at a time
            ingredientsSelectorLd.postValue(false);
        }
    };

    public RecipeDetailsViewModel(@NonNull Application application) {
        super(application);
        recipe = new MutableLiveData<>();
        stepIndexLd = new MutableLiveData<>();
        step = new MutableLiveData<>();
        nextStepAvailable = new ObservableBoolean(false);
        previousStepAvailable = new ObservableBoolean(false);
        ingredientsSelectorLd = new MutableLiveData<>();
        stepIndexLd.observeForever(stepIndexObserver);
    }

    public void loadRecipe(@NonNull Integer recipeId) {
        Timber.d("Load recipe: %d", recipeId);
        LoadRecipeTask loadRecipeTask = new LoadRecipeTask(getApplication(), recipeId, recipe);
        loadRecipeTask.execute();
        int stepIndex = getStepIndex();
        if (IntentArgs.isStepSelected(stepIndex)) {
            recipe.observeForever(new Observer<Recipe>() {
                @Override
                public void onChanged(Recipe aRecipe) {
                    recipe.removeObserver(this);
                    Timber.d("A recipe is loaded: %s", aRecipe);
                    List<Step> steps = aRecipe.getSteps();
                    if (steps != null && !steps.isEmpty()) {
                        step.postValue(steps.get(stepIndex));
                    }
                }
            });
        }
    }

    public MutableLiveData<Recipe> getRecipe() {
        return recipe;
    }

    public void selectStep(int stepIndex) {
        Timber.d("Setting a step index: %d", stepIndex);
        stepIndexLd.postValue(stepIndex);
        if (IntentArgs.STEP_NOT_SELECTED == stepIndex) {
            return;
        }
        Recipe recipe = this.recipe.getValue();
        if (recipe == null || recipe.getSteps() == null) {
            return;
        }
        this.step.postValue(recipe.getSteps().get(stepIndex));
        int totalSteps = recipe.getSteps().size();
        boolean nextStepFlag = stepIndex < totalSteps - 1;
        boolean prevStepFlag = stepIndex > 0;
        nextStepAvailable.set(nextStepFlag);
        previousStepAvailable.set(prevStepFlag);
    }

    public int getTotalSteps() {
        Recipe recipe = this.recipe.getValue();
        if (recipe == null || recipe.getSteps() == null) {
            return 0;
        }
        return recipe.getSteps().size();
    }

    public int getStepIndex() {
        return getStepIndex(step.getValue());
    }

    public int getStepIndex(Step step) {
        if (step == null) {
            return IntentArgs.STEP_NOT_SELECTED;
        }
        Recipe recipe = this.recipe.getValue();
        if (recipe == null) {
            return IntentArgs.STEP_NOT_SELECTED;
        }
        List<Step> steps = recipe.getSteps();
        if (steps == null) {
            return IntentArgs.STEP_NOT_SELECTED;
        }
        for (int i = 0; i < steps.size(); i++) {
            Step st = steps.get(i);
            if (st.getPk().equals(step.getPk())) {
                return i;
            }
        }
        return IntentArgs.STEP_NOT_SELECTED;
    }

    public MutableLiveData<Integer> getStepIndexLd() {
        return stepIndexLd;
    }

    public LiveData<Step> getStep() {
        return step;
    }

    @Nullable
    public Step getNextStep() {
        if (recipe.getValue() == null) {
            return null;
        }
        int stepIndex = getStepIndex(step.getValue());
        if (stepIndex < getTotalSteps() - 1) {
            return recipe.getValue().getSteps().get(stepIndex + 1);
        }
        return null;
    }

    @Nullable
    public Step getPrevStep() {
        if (recipe.getValue() == null) {
            return null;
        }
        int stepIndex = getStepIndex(step.getValue());
        if (stepIndex > 0) {
            return recipe.getValue().getSteps().get(stepIndex - 1);
        }
        return null;
    }

    public void selectIngredients() {
        ingredientsSelectorLd.postValue(true);
        clearSelectedStep();
    }

    private void clearSelectedStep() {
        selectStep(IntentArgs.STEP_NOT_SELECTED);
    }
}
