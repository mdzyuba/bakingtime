package com.mdzyuba.bakingtime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
@LargeTest
public class RecipeListActivityTest {

    @Rule
    public ActivityTestRule<RecipeListActivity> activityRule =
            new ActivityTestRule<>(RecipeListActivity.class);

    @Test
    public void recipesDisplayed() {
        onView(withId(R.id.item_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRecipesShowsRecipeDetails() {
        onView(withText("Brownies")).perform(click());
        onView(withId(R.id.item_detail_container)).check(matches(isDisplayed()));
    }

}