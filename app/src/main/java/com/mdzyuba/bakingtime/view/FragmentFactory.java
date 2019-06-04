package com.mdzyuba.bakingtime.view;

import android.os.Bundle;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.details.RecipeDetailFragment;
import com.mdzyuba.bakingtime.view.details.RecipeDetailsViewModel;
import com.mdzyuba.bakingtime.view.ingredients.IngredientsListFragment;
import com.mdzyuba.bakingtime.view.step.StepFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import timber.log.Timber;

public class FragmentFactory {
    private final static String FRAGMENT_TAG = StepFragment.class.getSimpleName();

    public static void showStepFragment(AppCompatActivity activity, int viewId,
                                        int stepIndex) {
        Bundle extras = activity.getIntent().getExtras();
        IntentArgs.setSelectedStep(extras, stepIndex);
        createStepFragment(activity, viewId, extras);
    }

    /**
     * Creates a StepFragment instance and passes a parameter that will enable viewing the
     * recipe video in a landscape mode only. This is used in a case where the fragment is
     * a part of the master detail flow.
     *
     * @param activity a parent activity.
     * @param viewId a resource id for the view.
     * @param detailsViewModel a view model. It is used to get the current step index.
     * @param step a step to be displayed.
     */
    public static void showStepFragmentLandscapeOnly(AppCompatActivity activity, int viewId,
                                                     RecipeDetailsViewModel detailsViewModel,
                                                     Step step) {
        Bundle extras = activity.getIntent().getExtras();
        if (extras == null) {
            Timber.e("The intent must have step parameters.");
            return;
        }
        IntentArgs.setSelectedStep(extras, detailsViewModel.getStepIndex(step));
        extras.putBoolean(StepFragment.ARG_LANDSCAPE_ONLY, true);
        createStepFragment(activity, viewId, extras);
    }

    private static void createStepFragment(AppCompatActivity activity, int viewId,
                                           Bundle extras) {
        StepFragment stepFragment = new StepFragment();
        stepFragment.setArguments(extras);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, stepFragment, FRAGMENT_TAG).commit();
    }

    public static void showRecipeDetailsFragment(AppCompatActivity activity) {
        Bundle arguments = new Bundle();
        int recipeId = activity.getIntent().getIntExtra(IntentArgs.ARG_RECIPE_ID, 0);
        int stepIndex = activity.getIntent().getIntExtra(IntentArgs.ARG_STEP_INDEX, IntentArgs.STEP_NOT_SELECTED);
        arguments.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        arguments.putInt(IntentArgs.ARG_STEP_INDEX, stepIndex);
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.item_detail_container, fragment)
                .commit();
    }

    public static void showIngredientsListFragment(AppCompatActivity activity, int recipeId) {
        IngredientsListFragment fragment = IngredientsListFragment.newInstance();
        Bundle arguments = new Bundle();
        arguments.putInt(IntentArgs.ARG_RECIPE_ID, recipeId);
        fragment.setArguments(arguments);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.step_details_container, fragment)
                .commitNow();
    }
}
