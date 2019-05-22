package com.mdzyuba.bakingtime;

import android.os.Bundle;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.view.list.RecipeListViewModel;
import com.mdzyuba.bakingtime.view.list.RecipeRecyclerViewAdapter;
import com.mdzyuba.bakingtime.view.list.RecipeSelectorListener;

import java.util.ArrayList;
import java.util.Collection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays a collection of Recipes.
 *
 * A click on a Recipe will open RecipeDetailActivity.
 */
public class RecipeListActivity extends AppCompatActivity implements RecipeSelectorListener {

    private RecipeListViewModel recipeListViewModel;

    @BindView(R.id.item_list)
    RecyclerView recyclerView;

    private final Observer<Collection<Recipe>> recipesObserver = new Observer<Collection<Recipe>>() {
        @Override
        public void onChanged(Collection<Recipe> recipes) {
            RecipeRecyclerViewAdapter adapter =
                    new RecipeRecyclerViewAdapter(RecipeListActivity.this,
                                                  new ArrayList<>(recipes));
            recyclerView.setAdapter(adapter);
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
    public void onRecipeSelected(Recipe recipe) {
        RecipeDetailActivity.startActivity(this, recipe.getId(), 0);
    }

}
