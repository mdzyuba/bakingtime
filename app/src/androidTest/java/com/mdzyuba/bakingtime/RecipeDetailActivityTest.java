package com.mdzyuba.bakingtime;

import android.content.Context;

import com.mdzyuba.bakingtime.utils.TestUtil;

import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class RecipeDetailActivityTest {
    private Context context;

    @Rule
    public ActivityScenarioRule<RecipeDetailActivity> activityScenarioRule
            = new ActivityScenarioRule<>(RecipeDetailActivity.getIntent(
            ApplicationProvider.getApplicationContext(), 1, 1));

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

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
    }

    @Test
    public void stepDetailsDisplayed() {
        onView(withText("Finish filling prep")).perform(click());
        onView(withText(StringContains.containsString("Beat the cream cheese")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnNextStepNavigationOpensNextStep() {
        onView(ViewMatchers.withId(R.id.rv_details))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.button_next)).check(matches(isDisplayed()));
        onView(withId(R.id.button_next)).perform(click());
        onView(withText(StringContains.containsString("Preheat the oven to 350")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnPrevStepNavigationOpensPreviousStep() {
        onView(ViewMatchers.withId(R.id.rv_details))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        onView(withId(R.id.button_prev)).check(matches(isDisplayed()));
        onView(withId(R.id.button_prev)).perform(click());

        if (TestUtil.isDualFrameMode(context)) {
            onView(withText(StringContains.containsString("Preheat the oven to 350"))).check(matches(isDisplayed()));
        } else {
            onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
            onView(withText("Recipe Introduction")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void stepDetailsHasVideoPlayer() {
        onView(ViewMatchers.withId(R.id.rv_details))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.video_player)).check(matches(isDisplayed()));
    }

}