package com.mdzyuba.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;
import com.mdzyuba.bakingtime.view.details.RecipeDetailFragment;
import com.mdzyuba.bakingtime.view.details.RecipeDetailsViewModel;
import com.mdzyuba.bakingtime.view.details.RecipeStepSelectorListener;
import com.mdzyuba.bakingtime.view.step.RecipeStepDetailsFragment;
import com.mdzyuba.bakingtime.view.step.VideoPlayerSingleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Displays a Recipe ingredients and steps.
 *
 * A click on a Recipe Step will open RecipeStepDetailsActivity.
 *
 * The activity requires RecipeDetailFragment.ARG_RECIPE_ID parameter.
 */
public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepSelectorListener,
                                                                       RecipeStepDetailsFragment.PlayerProvider {

    private RecipeDetailsViewModel detailsViewModel;

    /**
     * The detail container view will be present only in the
     * large-screen layouts (res/values-w900dp).
     * If this view is present, then the
     * activity should be in two-pane mode.
     */
    @Nullable
    @BindView(R.id.step_details_container)
    FrameLayout dualPaneFrame;

    public static void startActivity(Context context, int recipeId, int stepIndex) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(IntentArgs.ARG_RECIPE_ID, recipeId);
        intent.putExtra(IntentArgs.ARG_STEP_INDEX, stepIndex);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_details_frame);

        detailsViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);

        ButterKnife.bind(this);

        detailsViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                setTitle(recipe.getName());
            }
        });

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
            showRecipeDetailsFragment();

            if (isTwoPane()) {
                detailsViewModel.getStep().observe(this, new Observer<Step>() {
                    @Override
                    public void onChanged(Step step) {
                        detailsViewModel.getStep().removeObserver(this);
                        showStepDetailsFragment(step);
                    }
                });
            }
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
        Timber.d("Step selected: %s", step);
        detailsViewModel.setStepIndex(detailsViewModel.getStepIndex(step));
        if (isTwoPane()) {
            // TODO: try not reloading details fragment in the landscape mode. Update UI based on the model.
            Timber.d("showStepDetailsFragment");
            showStepDetailsFragment(step);
        } else {
            Timber.d("start RecipeStepDetailsActivity");
            Recipe recipe = detailsViewModel.getRecipe().getValue();
            if (recipe == null) {
                Timber.e("The recipe should be initialized");
                return;
            }
            RecipeStepDetailsActivity.startActivityWithStep(this, recipe.getId(),
                                                            detailsViewModel.getStepIndex(step));
        }
    }

    private void showRecipeDetailsFragment() {
        Bundle arguments = new Bundle();
        int recipeId = getIntent().getIntExtra(IntentArgs.ARG_RECIPE_ID, 0);
        int stepIndex = getIntent().getIntExtra(IntentArgs.ARG_STEP_INDEX, 0);
        arguments.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        arguments.putInt(IntentArgs.ARG_STEP_INDEX, stepIndex);
        Timber.d("show RecipeDetailFragment, recipeId: %d, stepIndex: %d", recipeId, stepIndex);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment)
                                   .commit();
    }

    private void showStepDetailsFragment(Step step) {
        Bundle arguments = new Bundle();
        int recipeId = step.getRecipeId();
        int stepIndex = detailsViewModel.getStepIndex(step);
        Timber.d("show RecipeStepDetailsFragment, recipeId: %d, stepIndex: %d", recipeId, stepIndex);
        arguments.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        arguments.putInt(IntentArgs.ARG_STEP_INDEX, stepIndex);

        RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
        recipeStepDetailsFragment.setArguments(arguments);
        recipeStepDetailsFragment.setItemDetailsSelectorListener(this);

        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.step_details_container, recipeStepDetailsFragment)
                                   .commit();
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
