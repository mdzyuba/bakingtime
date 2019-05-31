package com.mdzyuba.bakingtime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

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
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


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
        setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

        if (isDualFrameMode(ApplicationProvider.getApplicationContext())) {
            onView(withId(R.id.tv_description)).check(matches(isDisplayed()));

            setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
            onView(withId(R.id.tv_description)).check(doesNotExist());
        }
    }

    @Test
    public void nextStepNavigationWorksAfterRotation() throws Exception {
        setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

        if (!isDualFrameMode(ApplicationProvider.getApplicationContext())) {
            Timber.i("The test can be run on a tablet");
            return;
        }
        // click Recipe Introduction
        onView(withId(R.id.rv_details))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // rotate the device
        setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        delay();
        setOrientation(activity, (ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
        delay();

        onView(withId(R.id.button_next)).perform(click());
        // check the next step is displayed
        onView(withText(StringContains.containsString("Preheat the oven to 350")))
                .check(matches(isDisplayed()));
    }

    private void delay() throws InterruptedException {
        // TODO: replace the sleep with a better option.
        Thread.sleep(1500);
    }

    private boolean isDualFrameMode(Context context) {
        return context.getResources().getBoolean(R.bool.dual_pane_mode);
    }

    private void setOrientation(Activity activity, int newOrientation) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        activity.setRequestedOrientation(newOrientation);
        getInstrumentation().waitForIdle(new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

}
