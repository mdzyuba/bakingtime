package com.mdzyuba.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.FragmentFactory;
import com.mdzyuba.bakingtime.view.IntentArgs;
import com.mdzyuba.bakingtime.view.details.RecipeDetailsViewModel;
import com.mdzyuba.bakingtime.view.details.RecipeStepSelectorListener;

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
public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepSelectorListener {

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
                FragmentFactory.showStepFragmentLandscapeOnly(RecipeDetailActivity.this,
                                                              R.id.step_details_container,
                                                              detailsViewModel, step);
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
                FragmentFactory.showIngredientsListFragment(RecipeDetailActivity.this, recipeId);
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

        detailsViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);
        detailsViewModel.ingredientsSelectorLd.observe(this, ingredientsItemObserver);
        detailsViewModel.getRecipe().observe(this, recipeObserver);
        detailsViewModel.getStep().observe(this, stepObserver);

        if (savedInstanceState == null) {
            FragmentFactory.showRecipeDetailsFragment(this);
        } else {
            // Restoring the state.
            updateSelectedStep(savedInstanceState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStepSelected(@NonNull Step step) {
        int stepIndex = detailsViewModel.getStepIndex(step);
        detailsViewModel.selectStep(stepIndex);
        if (!isTwoPane()) {
            startStepDetailsActivity(stepIndex);
        }
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
        detailsViewModel.ingredientsSelectorLd.removeObserver(ingredientsItemObserver);
        detailsViewModel.getRecipe().removeObserver(recipeObserver);
        detailsViewModel.getStep().removeObserver(stepObserver);
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

    private void updateSelectedStep(Bundle savedInstanceState) {
        int stepIndex = IntentArgs.getSelectedStep(savedInstanceState);
        if (IntentArgs.isStepSelected(stepIndex)) {
            detailsViewModel.selectStep(stepIndex);
        }
    }
}
