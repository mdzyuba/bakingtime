package com.mdzyuba.bakingtime;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.mdzyuba.bakingtime.view.IntentArgs;
import com.mdzyuba.bakingtime.view.ingredients.IngredientsListFragment;

public class IngredientsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_list_activity);
        if (savedInstanceState == null) {

            IngredientsListFragment fragment = IngredientsListFragment.newInstance();
            Bundle arguments = getIntent().getExtras();
            if (arguments != null) {
                fragment.setArguments(arguments);
            }
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.container, fragment)
                                       .commitNow();
        }
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void startActivity(Context context, int recipeId) {
        Intent intent = new Intent(context, IngredientsListActivity.class);
        intent.putExtra(IntentArgs.ARG_RECIPE_ID, recipeId);
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
