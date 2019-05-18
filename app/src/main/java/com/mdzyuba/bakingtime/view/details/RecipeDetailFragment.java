package com.mdzyuba.bakingtime.view.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.RecipeDetailActivity;
import com.mdzyuba.bakingtime.RecipeListActivity;
import com.mdzyuba.bakingtime.model.Recipe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_RECIPE_ID = "recipeId";
    public static final String ARG_RECIPE_NAME = "recipeName";

    @BindView(R.id.rv_details)
    RecyclerView recyclerView;

    private RecipeDetailsViewModel detailsViewModel;
    private RecipeStepSelectorListener itemDetailsSelectorListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detailsViewModel = ViewModelProviders.of(this)
                                             .get(RecipeDetailsViewModel.class);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_RECIPE_ID)) {
            Integer recipeId = arguments.getInt(ARG_RECIPE_ID);
            detailsViewModel.loadRecipe(recipeId);
        }

        FragmentActivity activity = getActivity();
        if (activity instanceof RecipeStepSelectorListener) {
            itemDetailsSelectorListener = (RecipeStepSelectorListener) activity;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_details, container, false);

        ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        Observer<Recipe> recipeObserver = new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                RecipeDetailsViewAdapter viewAdapter = new RecipeDetailsViewAdapter(recipe, itemDetailsSelectorListener);
                recyclerView.setAdapter(viewAdapter);
            }
        };

        detailsViewModel.getRecipe().observe(this, recipeObserver);

        return rootView;
    }
}
