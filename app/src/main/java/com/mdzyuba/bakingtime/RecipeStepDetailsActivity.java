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
 */
public class RecipeStepDetailsActivity extends AppCompatActivity implements
                                                                 RecipeStepDetailsFragment.PlayerProvider,
                                                                 RecipeStepSelectorListener {

    private RecipeDetailsViewModel detailsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        detailsViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);

        detailsViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                Bundle arguments = getIntent().getExtras();
                if (arguments == null) {
                    Timber.e("No arguments provided. Unable to init the Recipe step");
                    return;
                }
                int stepIndex = arguments.getInt(IntentArgs.ARG_STEP_INDEX, 0);
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

    public static void startActivityWithStep(Context context, int recipeId, int stepIndex) {
        Intent intent = new Intent(context, RecipeStepDetailsActivity.class);
        intent.putExtra(IntentArgs.ARG_RECIPE_ID, recipeId);
        intent.putExtra(IntentArgs.ARG_STEP_INDEX, stepIndex);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RecipeDetailActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (isDualFrameMode()) {
            RecipeDetailActivity.startActivity(this, step.getRecipeId(),
                                               detailsViewModel.getStepIndex(step));
        } else {
            Recipe recipe = detailsViewModel.getRecipe().getValue();
            if (recipe != null) {
                RecipeStepDetailsActivity.startActivityWithStep(this, recipe.getId(),
                                                                detailsViewModel.getStepIndex(step));
            }
        }
    }

    private void showRecipeDetailsFragment() {
        RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
        recipeStepDetailsFragment.setArguments(getIntent().getExtras());
        recipeStepDetailsFragment.setItemDetailsSelectorListener(this);
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.recipe_step_details_frame, recipeStepDetailsFragment)
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
