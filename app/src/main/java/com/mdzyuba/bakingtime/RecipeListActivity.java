package com.mdzyuba.bakingtime;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.view.list.RecipeListViewModel;
import com.mdzyuba.bakingtime.view.list.RecipeRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays a collection of Recipes.
 *
 * A click on a Recipe will open RecipeDetailActivity.
 */
public class RecipeListActivity extends AppCompatActivity {

    private RecipeListViewModel recipeListViewModel;

    @BindView(R.id.item_list)
    RecyclerView recyclerView;

    /**
     * The detail container view will be present only in the
     * large-screen layouts (res/values-w900dp).
     * If this view is present, then the
     * activity should be in two-pane mode.
     */
    @Nullable
    @BindView(R.id.item_detail_container)
    FrameLayout dualPaneFrame;

    private final Observer<Collection<Recipe>> recipesObserver = new Observer<Collection<Recipe>>() {
        @Override
        public void onChanged(Collection<Recipe> recipes) {
            recyclerView.setAdapter(new RecipeRecyclerViewAdapter(RecipeListActivity.this,
                                                                  new ArrayList<>(recipes),
                                                                  isTwoPane()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        recipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        recipeListViewModel.getRecipes().observe(this, recipesObserver);
    }

    /**
     * Checks if the device screen wide enough to hold two panes for the list and details views.
     *
     * @return true if two panes is supported.
     */
    private boolean isTwoPane() {
        return dualPaneFrame != null;
    }
}
