package com.mdzyuba.bakingtime;

import android.content.Context;

import com.mdzyuba.bakingtime.repository.RecipeFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
@LargeTest
public class RecipeListActivityTest {

    private Context context;

    @Rule
    public ActivityTestRule<RecipeListActivity> activityRule =
            new ActivityTestRule<>(RecipeListActivity.class);

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void recipesDisplayed() {
        onView(withId(R.id.item_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRecipesShowsRecipeDetails() {
        onView(withText("Brownies")).perform(click());
        onView(withId(R.id.item_detail_container)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRefreshRecipesMenuReloadsRecipes() {
        RecipeFactory factory = new RecipeFactory(context);
        factory.cleanDb();

        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Refresh Recipes")).perform(click());

        // Checking for the progress bar is not an option at this time.
        // https://stackoverflow.com/questions/35186902/testing-progress-bar-on-android-with-espresso

        onView(withText("Brownies")).check(matches(isDisplayed()));
    }

}