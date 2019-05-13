package com.mdzyuba.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.step.RecipeStepDetailsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays a Recipe Step information.
 */
public class RecipeStepDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
        recipeStepDetailsFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.recipe_step_details_frame, recipeStepDetailsFragment)
                                   .commit();
    }

    public static void startActivity(Context context, @NonNull Step step) {
        Intent intent = new Intent(context, RecipeStepDetailsActivity.class);
        intent.putExtra(RecipeStepDetailsFragment.ARG_RECIPE_STEP_ID, step.getPk());
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RecipeDetailActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
