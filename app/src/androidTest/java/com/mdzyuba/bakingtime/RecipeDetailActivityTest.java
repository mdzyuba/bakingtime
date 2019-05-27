package com.mdzyuba.bakingtime;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class RecipeDetailActivityTest {

    @Rule
    public ActivityScenarioRule<RecipeDetailActivity> activityScenarioRule
            = new ActivityScenarioRule<>(RecipeDetailActivity.getIntent(
            ApplicationProvider.getApplicationContext(), 1, 1));

    @Test
    public void activityTitleDisplayesRecipeName() {
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));
    }

    @Test
    public void recipeIngredientsLabelIsDisplaed() {
        onView(withId(R.id.tv_ingredients_label)).check(matches(isDisplayed()));
        onView(withText("Ingredients")).check(matches(isDisplayed()));
    }

    @Test
    public void recipeStepsListIsDisplaed() {
        onView(withId(R.id.rv_details)).check(matches(isDisplayed()));
        onView(withText("Finishing Steps")).check(matches(isDisplayed()));
    }

}