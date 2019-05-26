package com.mdzyuba.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;
import com.mdzyuba.bakingtime.view.details.RecipeDetailsViewModel;
import com.mdzyuba.bakingtime.view.details.RecipeStepSelectorListener;
import com.mdzyuba.bakingtime.view.step.RecipeStepDetailsFragment;
import com.mdzyuba.bakingtime.view.step.VideoPlayerSingleton;

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
public class RecipeStepDetailsActivity extends AppCompatActivity implements
                                                                 RecipeStepDetailsFragment.PlayerProvider,
                                                                 RecipeStepSelectorListener {
    private RecipeDetailsViewModel detailsViewModel;

    public static Intent getActivityForResultIntent(Context context, int recipeId, int stepIndex) {
        Intent intent = new Intent(context, RecipeStepDetailsActivity.class);
        IntentArgs.setArgs(intent, recipeId, stepIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_step_details_activity);

        detailsViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);

        detailsViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
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
                detailsViewModel.setStepIndex(stepIndex);
            }
        });

        detailsViewModel.getStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(Step step) {
                Timber.d("Step is updated: %s", step);
                setTitle(step.getShortDescription());
                showRecipeDetailsFragment();
            }
        });

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
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerSingleton.getInstance(this).releasePlayer();
    }

    @Override
    public SimpleExoPlayer getPlayer() {
        return VideoPlayerSingleton.getInstance(this).getExoPlayer(this);
    }

    @Override
    public void onStepSelected(@NonNull Step step) {
        int stepIndex = detailsViewModel.getStepIndex(step);
        if (isDualFrameMode()) {
            RecipeDetailActivity.startActivity(this, step.getRecipeId(),
                                               stepIndex);
        } else {
            saveSelectedStepToActivityResult(stepIndex);
            Recipe recipe = detailsViewModel.getRecipe().getValue();
            if (recipe != null) {
                Timber.d("Navigating to step: %s", step);
                detailsViewModel.setStepIndex(stepIndex);
            }
        }
    }

    private void saveSelectedStepToActivityResult(int stepIndex) {
        Recipe recipe = detailsViewModel.getRecipe().getValue();
        if (recipe != null) {
            Intent resultIntent = new Intent();
            IntentArgs.setArgs(resultIntent, recipe.getId(), stepIndex);
            setResult(RESULT_OK, resultIntent);
        }
    }

    private void showRecipeDetailsFragment() {
        final String FRAGMENT_TAG = RecipeStepDetailsFragment.class.getSimpleName();
        RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
        Bundle extras = getIntent().getExtras();
        IntentArgs.setSelectedStep(extras, detailsViewModel.getStepIndex());
        recipeStepDetailsFragment.setArguments(extras);
        recipeStepDetailsFragment.setItemDetailsSelectorListener(this);
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.recipe_step_details_frame,
                                            recipeStepDetailsFragment,
                                            FRAGMENT_TAG)
                                   .commit();
    }

    private boolean isLandscapeOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private boolean isDualFrameMode() {
        return getResources().getBoolean(R.bool.dual_pane_mode);
    }

}
