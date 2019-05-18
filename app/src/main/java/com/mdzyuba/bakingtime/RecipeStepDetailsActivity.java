package com.mdzyuba.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.details.RecipeStepSelectorListener;
import com.mdzyuba.bakingtime.view.step.RecipeStepDetailsFragment;
import com.mdzyuba.bakingtime.view.step.VideoPlayerSingleton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays a Recipe Step information.
 */
public class RecipeStepDetailsActivity extends AppCompatActivity implements
                                                                 RecipeStepDetailsFragment.PlayerProvider,
                                                                 RecipeStepSelectorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (isLandscapeOrientation()) {
                actionBar.hide();
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
        recipeStepDetailsFragment.setArguments(getIntent().getExtras());
        recipeStepDetailsFragment.setItemDetailsSelectorListener(this);
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.recipe_step_details_frame, recipeStepDetailsFragment)
                                   .commit();

        String stepName = getIntent().getStringExtra(RecipeStepDetailsFragment.ARG_RECIPE_STEP_NAME);
        if (stepName != null) {
            setTitle(stepName);
        }
    }

    public static void startActivity(Context context, @NonNull Step step) {
        Intent intent = new Intent(context, RecipeStepDetailsActivity.class);
        intent.putExtra(RecipeStepDetailsFragment.ARG_RECIPE_STEP_ID, step.getPk());
        intent.putExtra(RecipeStepDetailsFragment.ARG_RECIPE_STEP_NAME, step.getShortDescription());
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
        RecipeStepDetailsActivity.startActivity(this, step);
    }

    private boolean isLandscapeOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

}
