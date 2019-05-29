package com.mdzyuba.bakingtime;

import android.content.Context;

import com.mdzyuba.bakingtime.repository.RecipeFactory;
import com.mdzyuba.testutil.BuildConfig;
import com.mdzyuba.testutil.TestConfig;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import androidx.test.core.app.ApplicationProvider;

@RunWith(Suite.class)
@Suite.SuiteClasses({RecipeListActivityTest.class, RecipeDetailActivityTest.class})
public class TestSuite {
    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        TestConfig.setupInstrumentationTestDb(context, BuildConfig.TEST_BAKER_URL);
        RecipeFactory recipeFactory = new RecipeFactory(context);
        recipeFactory.loadRecipes(context);
    }
}
