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
import com.mdzyuba.bakingtime.view.ingredients.IngredientsListFragment;
import com.mdzyuba.bakingtime.view.step.StepFragment;
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
                                                                       StepFragment.PlayerProvider {

    private static final int SELECT_STEP_REQUEST = 1;

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

    private final Observer<Recipe> recipeObserver = new Observer<Recipe>() {
        @Override
        public void onChanged(Recipe recipe) {
            setTitle(recipe.getName());
            // Display the first step by default
            if (isTwoPane() &&
                detailsViewModel.getStepIndex() == IntentArgs.STEP_NOT_SELECTED &&
                detailsViewModel.getTotalSteps() > 0) {
                detailsViewModel.selectStep(0);
            }
        }
    };

    private final Observer<Step> stepObserver = new Observer<Step>() {
        @Override
        public void onChanged(Step step) {
            if (isTwoPane()) {
                showStepDetailsFragment(step);
            }
        }
    };

    private final Observer<Boolean> ingredientsItemObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean ingredientsSelected) {
            Recipe recipe = detailsViewModel.getRecipe().getValue();
            if (!ingredientsSelected || recipe == null) {
                return;
            }
            int recipeId = recipe.getId();
            if (isTwoPane()) {
                showIngredientsListFragment(recipeId);
            } else {
                IngredientsListActivity.startActivity(RecipeDetailActivity.this, recipeId);
            }
        }
    };

    public static void startActivity(Context context, int recipeId, int stepIndex) {
        Intent intent = getIntent(context, recipeId, stepIndex);
        context.startActivity(intent);
    }

    public static Intent getIntent(Context context, int recipeId, int stepIndex) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(IntentArgs.ARG_RECIPE_ID, recipeId);
        intent.putExtra(IntentArgs.ARG_STEP_INDEX, stepIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_details_frame_activity);
        ButterKnife.bind(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (detailsViewModel == null) {
            detailsViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);
            detailsViewModel.getRecipe().observe(this, recipeObserver);
            detailsViewModel.ingredientsSelectorLd.observe(this, ingredientsItemObserver);
            detailsViewModel.getStep().observe(this, stepObserver);
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
        } else {
            updateSelectedStep(savedInstanceState);
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
        int stepIndex = detailsViewModel.getStepIndex(step);
        detailsViewModel.selectStep(stepIndex);
        if (isTwoPane()) {
            return;
        }
        startStepDetailsActivity(stepIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && SELECT_STEP_REQUEST == requestCode && data != null) {
            updateSelectedStep(data.getExtras());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Recipe recipe = detailsViewModel.getRecipe().getValue();
        if (recipe == null) {
            return;
        }
        int recipeId = recipe.getId();
        int stepIndex = detailsViewModel.getStepIndex();
        Timber.d("saving Instance State recipeId: %d, stepIndex: %d", recipeId, stepIndex);
        outState.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        outState.putInt(IntentArgs.ARG_STEP_INDEX, stepIndex);
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

    private void startStepDetailsActivity(int stepIndex) {
        Recipe recipe = detailsViewModel.getRecipe().getValue();
        if (recipe == null) {
            Timber.e("The recipe should be initialized");
            return;
        }
        Intent intent = StepActivity
                .getActivityForResultIntent(this, recipe.getId(), stepIndex);
        startActivityForResult(intent, SELECT_STEP_REQUEST);
    }

    private void showRecipeDetailsFragment() {
        Bundle arguments = new Bundle();
        int recipeId = getIntent().getIntExtra(IntentArgs.ARG_RECIPE_ID, 0);
        int stepIndex = getIntent().getIntExtra(IntentArgs.ARG_STEP_INDEX, IntentArgs.STEP_NOT_SELECTED);
        arguments.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        arguments.putInt(IntentArgs.ARG_STEP_INDEX, stepIndex);
        Timber.d("show RecipeDetailFragment, recipeId: %d, stepIndex: %d", recipeId, stepIndex);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment)
                                   .commit();
    }

    private void showIngredientsListFragment(int recipeId) {
        IngredientsListFragment fragment = IngredientsListFragment.newInstance();
        Bundle arguments = new Bundle();
        arguments.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.step_details_container, fragment)
                                   .commitNow();
    }

    private void showStepDetailsFragment(Step step) {
        Bundle arguments = new Bundle();
        int recipeId = step.getRecipeId();
        int stepIndex = detailsViewModel.getStepIndex(step);
        Timber.d("show RecipeStepDetailsFragment, recipeId: %d, stepIndex: %d", recipeId, stepIndex);
        arguments.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        arguments.putInt(IntentArgs.ARG_STEP_INDEX, stepIndex);

        StepFragment recipeStepDetailsFragment = new StepFragment();
        recipeStepDetailsFragment.setArguments(arguments);
        recipeStepDetailsFragment.setItemDetailsSelectorListener(this);

        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.step_details_container, recipeStepDetailsFragment)
                                   .commit();
    }

    private void updateSelectedStep(Bundle savedInstanceState) {
        int stepIndex = IntentArgs.getSelectedStep(savedInstanceState);
        if (IntentArgs.isStepSelected(stepIndex)) {
            detailsViewModel.selectStep(stepIndex);
        }
    }
}
