package com.mdzyuba.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.view.IntentArgs;
import com.mdzyuba.bakingtime.view.details.ErrorDialog;
import com.mdzyuba.bakingtime.view.list.RecipeListViewModel;
import com.mdzyuba.bakingtime.view.list.RecipeRecyclerViewAdapter;
import com.mdzyuba.bakingtime.view.list.RecipeSelectorListener;
import com.mdzyuba.bakingtime.widget.BakingTimeWidgetProvider;

import java.util.ArrayList;
import java.util.Collection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Displays a collection of Recipes.
 *
 * A click on a Recipe will open RecipeDetailActivity.
 */
public class RecipeListActivity extends AppCompatActivity implements RecipeSelectorListener {

    @BindView(R.id.item_list)
    RecyclerView recyclerView;

    @BindView(R.id.loading_recipes_progress)
    LinearLayout progressView;

    private RecipeListViewModel recipeListViewModel;
    private RecipeRecyclerViewAdapter adapter;

    private final Observer<Collection<Recipe>> recipesObserver = new Observer<Collection<Recipe>>() {
        @Override
        public void onChanged(Collection<Recipe> recipes) {
            if (recipes == null) {
                Timber.e("Unable to load recipes");
                ErrorDialog.showErrorDialog(RecipeListActivity.this, new ErrorDialog.Retry() {
                    @Override
                    public void retry() {
                        recipeListViewModel.loadRecipes();
                    }
                });
                return;
            }

            if (adapter == null) {
                adapter = new RecipeRecyclerViewAdapter(RecipeListActivity.this,
                                                        new ArrayList<>(recipes));
                recyclerView.setAdapter(adapter);
            }

            updateRecipeLoadingProgress(recipes);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list_activity);
        ButterKnife.bind(this);

        recipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        recipeListViewModel.getRecipes().observe(this, recipesObserver);

        int columns = getResources().getInteger(R.integer.recipe_grid_columns);
        if (columns > 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_refresh == item.getItemId()) {
            recipeListViewModel.reloadRecipes();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        RecipeDetailActivity.startActivity(this, recipe.getId(), IntentArgs.STEP_NOT_SELECTED);
        updateWidget(recipe);
    }

    private void updateWidget(Recipe recipe) {
        Timber.d("Updating recipe: %d", recipe.getId());
        Intent intent = new Intent(this, BakingTimeWidgetProvider.class);
        intent.setAction(BakingTimeWidgetProvider.ACTION_UPDATE_RECIPE);
        intent.putExtra(IntentArgs.ARG_RECIPE_ID, recipe.getId());
        sendBroadcast(intent);
    }

    private void updateRecipeLoadingProgress(Collection<Recipe> recipes) {
        if (recipes.isEmpty()) {
            progressView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
