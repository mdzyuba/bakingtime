package com.mdzyuba.bakingtime.view.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdzyuba.bakingtime.IngredientsListActivity;
import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.RecipeDetailActivity;
import com.mdzyuba.bakingtime.RecipeListActivity;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {

    @BindView(R.id.rv_details)
    RecyclerView recyclerView;

    @BindView(R.id.tv_ingredients_label)
    TextView ingredients;

    private RecipeDetailsViewModel detailsViewModel;
    private RecipeStepSelectorListener itemDetailsSelectorListener;
    private RecipeDetailsViewAdapter viewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("The activity is null. Unable to create the fragment.");
            return;
        }

        detailsViewModel = ViewModelProviders.of(activity).get(RecipeDetailsViewModel.class);

        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(IntentArgs.ARG_RECIPE_ID)) {
                int recipeId = arguments.getInt(IntentArgs.ARG_RECIPE_ID);
                if (detailsViewModel.getRecipe().getValue() == null ||
                    recipeId != detailsViewModel.getRecipe().getValue().getId()) {
                    detailsViewModel.loadRecipe(recipeId);
                }
            }
        }

        if (activity instanceof RecipeStepSelectorListener) {
            itemDetailsSelectorListener = (RecipeStepSelectorListener) activity;
        }

        detailsViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                Bundle arguments = getArguments();
                if (arguments == null) {
                    Timber.e("The fragment arguments are null. Unable to init the Recipe step.");
                    return;
                }
                int stepIndex = arguments.getInt(IntentArgs.ARG_STEP_INDEX, 0);
                Timber.d("Setting a step index %d", stepIndex);
                detailsViewModel.setStepIndex(stepIndex);
            }
        });

        detailsViewModel.getStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(Step step) {
                Timber.d("step changed - setSelectedStepPk: %s", step);
                if (viewAdapter != null) {
                    viewAdapter.setSelectedStepPk(step.getPk());
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_details_fragment, container, false);

        ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        Observer<Recipe> recipeObserver = new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                viewAdapter = new RecipeDetailsViewAdapter(recipe, itemDetailsSelectorListener);
                recyclerView.setAdapter(viewAdapter);
            }
        };

        detailsViewModel.getRecipe().observe(this, recipeObserver);

        ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = detailsViewModel.getRecipe().getValue();
                if (recipe != null) {
                    IngredientsListActivity.startActivity(getContext(), recipe.getId());
                }
            }
        });

        return rootView;
    }

}
