package com.mdzyuba.bakingtime;

import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
    public void activityTitleDisplaysRecipeName() {
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));
    }

    @Test
    public void recipeIngredientsLabelIsDisplayed() {
        onView(withId(R.id.tv_ingredients_label)).check(matches(isDisplayed()));
        onView(withText("Ingredients")).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnIngredientsOpensIngredientsList() {
        onView(withText("Ingredients")).perform(click());
        onView(withText("salt")).check(matches(isDisplayed()));
    }

    @Test
    public void recipeStepsListIsDisplayed() {
        onView(withId(R.id.rv_details)).check(matches(isDisplayed()));
        onView(withText("Finishing Steps")).check(matches(isDisplayed()));
    }

    @Test
    public void stepDetailsDisplayed() {
        onView(withText("Finish filling prep")).perform(click());
        onView(withText(StringContains.containsString("Beat the cream cheese")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnNextStepNavigationOpensNextStep() {
        onView(withText("Recipe Introduction")).perform(click());
        onView(withId(R.id.button_next)).check(matches(isDisplayed()));
        onView(withId(R.id.button_next)).perform(click());
        onView(withText(StringContains.containsString("Preheat the oven to 350")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnPrevStepNavigationOpensPreviousStep() {
        onView(withText("Finishing Steps")).perform(click());
        onView(withId(R.id.button_prev)).check(matches(isDisplayed()));
        onView(withId(R.id.button_prev)).perform(click());
        onView(withText(StringContains.containsString("Beat the cream cheese and 50 grams")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void stepDetailsHasVideoPlayer() {
        onView(withText("Recipe Introduction")).perform(click());
        onView(withId(R.id.video_player)).check(matches(isDisplayed()));
    }

}