package com.mdzyuba.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.details.RecipeStepSelectorListener;
import com.mdzyuba.bakingtime.view.details.RecipeDetailFragment;
import com.mdzyuba.bakingtime.view.step.RecipeStepDetailsFragment;
import com.mdzyuba.bakingtime.view.step.VideoPlayerSingleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays a Recipe ingredients and steps.
 *
 * A click on a Recipe Step will open RecipeStepDetailsActivity.
 */
public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepSelectorListener,
                                                                       RecipeStepDetailsFragment.PlayerProvider {

    private String recipeName;

    /**
     * The detail container view will be present only in the
     * large-screen layouts (res/values-w900dp).
     * If this view is present, then the
     * activity should be in two-pane mode.
     */
    @Nullable
    @BindView(R.id.step_details_container)
    FrameLayout dualPaneFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_details_frame);

        ButterKnife.bind(this);

        if (recipeName == null) {
            recipeName = getIntent().getStringExtra(RecipeDetailFragment.ARG_RECIPE_NAME);
        }
        setTitle(recipeName);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            int recipeId = getIntent().getIntExtra(RecipeDetailFragment.ARG_RECIPE_ID, 0);
            arguments.putInt(RecipeDetailFragment.ARG_RECIPE_ID, recipeId);
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment)
                                       .commit();
        }
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
            navigateUpTo(new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStepSelected(@NonNull Step step) {
        if (isTwoPane()) {
            Bundle arguments = new Bundle();
            arguments.putInt(RecipeStepDetailsFragment.ARG_RECIPE_STEP_ID, step.getPk());
            arguments.putString(RecipeStepDetailsFragment.ARG_RECIPE_STEP_NAME, step.getShortDescription());

            RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
            recipeStepDetailsFragment.setArguments(arguments);
            recipeStepDetailsFragment.setItemDetailsSelectorListener(this);

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.step_details_container, recipeStepDetailsFragment)
                                       .commit();
        } else {
            RecipeStepDetailsActivity.startActivity(this, step);
        }
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

    /**
     * Checks if the device screen wide enough to hold two panes for the list and details views.
     *
     * @return true if two panes is supported.
     */
    private boolean isTwoPane() {
        return dualPaneFrame != null;
    }
}
