package com.mdzyuba.bakingtime;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import com.mdzyuba.bakingtime.utils.TestUtil;

import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import timber.log.Timber;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeMasterDetailViewTest {

    @Rule
    public ActivityTestRule<RecipeDetailActivity> mActivityRule = new ActivityTestRule<RecipeDetailActivity>(
            RecipeDetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            return RecipeDetailActivity.getIntent(ApplicationProvider.getApplicationContext(), 1, 0);
        }
    };

    private RecipeDetailActivity activity = null;

    @Before
    public void setActivity() {
        activity = mActivityRule.getActivity();
    }

    @Test
    public void stepDescriptionIsDisplayedInMasterDetailsView() throws Exception {
        onView(withId(R.id.rv_details)).perform(RecyclerViewActions.actionOnItemAtPosition(5, click()));
        TestUtil.setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

        if (TestUtil.isDualFrameMode(ApplicationProvider.getApplicationContext())) {
            onView(withId(R.id.tv_description)).check(matches(isDisplayed()));

            TestUtil.setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
            onView(withId(R.id.tv_description)).check(doesNotExist());
        }
    }

    @Test
    public void nextStepNavigationWorksAfterRotation() throws Exception {
        TestUtil.setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

        if (!TestUtil.isDualFrameMode(ApplicationProvider.getApplicationContext())) {
            Timber.i("The test can be run on a tablet");
            return;
        }
        // Click Recipe Introduction.
        onView(withId(R.id.rv_details))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Rotate the device.
        TestUtil.setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

        // Even though the setOrientation() is using CountDownLatch, the test is still flaky
        // unless we add a short wait after the device rotation. I hope there is a better way.
        // TODO: replace the sleep with a better option.
        TestUtil.delay();

        TestUtil.setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
        TestUtil.delay();

        onView(withId(R.id.button_next)).perform(click());
        // check the next step is displayed
        onView(withText(StringContains.containsString("Preheat the oven to 350")))
                .check(matches(isDisplayed()));
    }
}
