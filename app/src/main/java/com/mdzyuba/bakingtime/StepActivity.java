package com.mdzyuba.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;
import com.mdzyuba.bakingtime.view.details.RecipeDetailsViewModel;
import com.mdzyuba.bakingtime.view.details.RecipeStepSelectorListener;
import com.mdzyuba.bakingtime.view.FragmentFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import timber.log.Timber;

/**
 * Displays a Recipe Step information.
 *
 * The activity provides options to navigate between Recipe steps. It is passing selected step
 * back to the RecipeDetailActivity by setting activity result, so the RecipeDetailActivity
 * could highlight the currently selected step. The activity result is preserved on back button
 * click as well as home button click.
 */
public class StepActivity extends AppCompatActivity implements RecipeStepSelectorListener {

    private RecipeDetailsViewModel detailsViewModel;
    private final Observer<Recipe> recipeObserver = new Observer<Recipe>() {
        @Override
        public void onChanged(Recipe recipe) {
            detailsViewModel.getRecipe().removeObserver(this);
            if (detailsViewModel.getStepIndex() > IntentArgs.STEP_NOT_SELECTED) {
                Timber.d("The model has step index: %d", detailsViewModel.getStepIndex());
                return;
            }
            Bundle arguments = getIntent().getExtras();
            if (arguments == null) {
                Timber.e("No arguments provided. Unable to init the Recipe step");
                return;
            }
            int stepIndex = IntentArgs.getSelectedStep(arguments);
            detailsViewModel.selectStep(stepIndex);
        }
    };

    private final Observer<Step> stepObserver = new Observer<Step>() {
        @Override
        public void onChanged(Step step) {
            detailsViewModel.getStep().removeObserver(this);
            Timber.d("Step is updated: %s", step);
            showStep(step);
        }
    };

    public static Intent getActivityForResultIntent(Context context, int recipeId, int stepIndex) {
        Intent intent = new Intent(context, StepActivity.class);
        IntentArgs.setArgs(intent, recipeId, stepIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate %s", savedInstanceState);
        setContentView(R.layout.step_activity);

        detailsViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);
        if (savedInstanceState == null) {
            detailsViewModel.getRecipe().observe(this, recipeObserver);
            detailsViewModel.getStep().observe(this, stepObserver);
        }

        if (savedInstanceState == null) {
            Bundle arguments = getIntent().getExtras();
            if (arguments != null) {
                if (arguments.containsKey(IntentArgs.ARG_RECIPE_ID)) {
                    int recipeId = arguments.getInt(IntentArgs.ARG_RECIPE_ID);
                    detailsViewModel.loadRecipe(recipeId);
                }
            }
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (isLandscapeOrientation()) {
                actionBar.hide();
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Return a selected step to the parent activity.
            saveSelectedStepToActivityResult(detailsViewModel.getStepIndex());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Return a selected step to the parent activity.
        saveSelectedStepToActivityResult(detailsViewModel.getStepIndex());
        super.onBackPressed();
    }

    @Override
    public void onStepSelected(@NonNull Step step) {
        int stepIndex = detailsViewModel.getStepIndex(step);
        if (isDualFrameMode()) {
            RecipeDetailActivity.startActivity(this, step.getRecipeId(), stepIndex);
        } else {
            saveSelectedStepToActivityResult(stepIndex);
            Recipe recipe = detailsViewModel.getRecipe().getValue();
            if (recipe != null) {
                Timber.d("Selected step: %s", step);
                detailsViewModel.selectStep(stepIndex);
                showStep(step);
            }
        }
    }

    private void showStep(Step step) {
        setTitle(step.getShortDescription());
        int stepIndex = detailsViewModel.getStepIndex(step);
        FragmentFactory.showStepFragment(this,
                                         R.id.recipe_step_details_frame,
                                         stepIndex);
    }

    private void saveSelectedStepToActivityResult(int stepIndex) {
        Recipe recipe = detailsViewModel.getRecipe().getValue();
        if (recipe != null) {
            Intent resultIntent = new Intent();
            IntentArgs.setArgs(resultIntent, recipe.getId(), stepIndex);
            setResult(RESULT_OK, resultIntent);
        }
    }

    private boolean isLandscapeOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private boolean isDualFrameMode() {
        return getResources().getBoolean(R.bool.dual_pane_mode);
    }

}
