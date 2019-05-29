package com.mdzyuba.bakingtime.repository;

import android.content.Context;

import com.mdzyuba.bakingtime.model.Ingredient;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.testutil.BuildConfig;
import com.mdzyuba.testutil.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.test.core.app.ApplicationProvider;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class RecipeFactoryTest {

    private static final String BAKING_JSON = "baking.json";

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        TestConfig.setupTestDb(context, BuildConfig.TEST_BAKER_URL);
    }

    @Test
    public void loadRecipes_parsesJson() throws Exception {
        String json = TestDataUtils.getJsonString(BAKING_JSON);
        Context context = ApplicationProvider.getApplicationContext();
        RecipeFactory factory = new RecipeFactory(context);
        Collection<Recipe> recipes = factory.loadRecipes(json);
        assertNotNull(recipes);
        assertEquals(4, recipes.size());
        System.out.println(recipes);

        Recipe recipe = recipes.toArray(new Recipe[0])[0];
        assertEquals("Nutella Pie", recipe.getName());
        assertEquals(8, recipe.getServings());
        assertEquals("", recipe.getImage());

        List<Ingredient> ingredients = recipe.getIngredients();
        assertEquals(9, ingredients.size());
        Ingredient[] ingredientsArray = ingredients.toArray(new Ingredient[0]);
        Ingredient ingr = ingredientsArray[0];
        System.out.println(ingr);
        assertEquals(2, ingr.getQuantity(), 0.01);
        assertEquals("CUP", ingr.getMeasure());
        assertEquals("Graham Cracker crumbs", ingr.getIngredient());

        List<Step> steps = recipe.getSteps();
        Step step = steps.toArray(new Step[0])[6];
        assertEquals(6, (int) step.getId());
        assertEquals("Finishing Steps", step.getShortDescription());
        assertEquals(
                "6. Pour the filling into the prepared crust and smooth the top. Spread the " +
                "whipped cream over the filling. Refrigerate the pie for at least 2 hours. Then " +
                "it's ready to serve!",
                step.getDescription());
        assertEquals(
                "https://d17h27t6h515a5.cloudfront" +
                ".net/topher/2017/April/58ffda45_9-add-mixed-nutella-to-crust-creampie/9-add" +
                "-mixed-nutella-to-crust-creampie.mp4",
                step.getVideoURL());
        assertEquals("", step.getThumbnailURL());
    }
}