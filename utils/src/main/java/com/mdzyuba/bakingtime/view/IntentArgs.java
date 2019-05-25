package com.mdzyuba.bakingtime.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class IntentArgs {
    public static final String ARG_RECIPE_ID = "recipeId";
    public static final String ARG_STEP_INDEX = "stepIndex";
    public static final int STEP_NOT_SELECTED = -1;

    public static  int getSelectedStep(Bundle arguments) {
        if (arguments == null) {
            Timber.e("The fragment arguments are null. Unable to init the Recipe step.");
            return IntentArgs.STEP_NOT_SELECTED;
        }
        return arguments.getInt(IntentArgs.ARG_STEP_INDEX, IntentArgs.STEP_NOT_SELECTED);
    }

    public static boolean isStepSelected(Bundle arguments) {
        int stepIndex = getSelectedStep(arguments);
        return isStepSelected(stepIndex);
    }

    public static boolean isStepSelected(int stepIndex) {
        return stepIndex > IntentArgs.STEP_NOT_SELECTED;
    }

    public static void setSelectedStep(Bundle arguments, int stepIndex) {
       if (arguments == null) {
            Timber.e("The intent data is null. Unable to init the recipe details fragment.");
            return;
        }
        arguments.putInt(IntentArgs.ARG_STEP_INDEX, stepIndex);
    }

    public static void setArgs(@NonNull Intent intent, int recipeId, int stepIndex) {
        intent.putExtra(IntentArgs.ARG_RECIPE_ID, recipeId);
        intent.putExtra(IntentArgs.ARG_STEP_INDEX, stepIndex);
    }
}
