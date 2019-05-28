package com.mdzyuba.bakingtime.view.details;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.RecipeDetailActivity;
import com.mdzyuba.bakingtime.RecipeListActivity;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
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
    private IngredientsSelectorListener ingredientsSelectorListener;

    private final Observer<Recipe> recipeObserver = new Observer<Recipe>() {
        @Override
        public void onChanged(Recipe recipe) {
            if (viewAdapter == null) {
                initRecyclerViewAdapter(recipe);
            }
            if (IntentArgs.isStepSelected(getArguments())) {
                int stepIndex = IntentArgs.getSelectedStep(getArguments());
                Timber.d("Setting a step index %d", stepIndex);
                detailsViewModel.setStepIndex(stepIndex);
            }
        }
    };

    private final Observer<Step> stepObserver = new Observer<Step>() {
        @Override
        public void onChanged(Step step) {
            Timber.d("step changed - setSelectedStepPk: %s", step);
            if (viewAdapter != null) {
                viewAdapter.setSelectedStepPk(step.getPk());
                scrollToSelectedStep();
            }
        }
    };

    private final Observer<Integer> stepIndexObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer stepIndex) {
            if (stepIndex == IntentArgs.STEP_NOT_SELECTED) {
                if (viewAdapter != null) {
                    viewAdapter.clearSelectedStep();
                }
            }
        }
    };

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

        if (activity instanceof RecipeStepSelectorListener) {
            itemDetailsSelectorListener = (RecipeStepSelectorListener) activity;
        }

        detailsViewModel = ViewModelProviders.of(activity).get(RecipeDetailsViewModel.class);
        detailsViewModel.getRecipe().observe(this, recipeObserver);
        detailsViewModel.getStep().observe(this, stepObserver);
        detailsViewModel.getStepIndexLd().observe(this, stepIndexObserver);

        if (savedInstanceState == null) {
            loadRecipe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detailsViewModel.getRecipe().removeObserver(recipeObserver);
        detailsViewModel.getStep().removeObserver(stepObserver);
        detailsViewModel.getStepIndexLd().removeObserver(stepIndexObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_details_fragment, container, false);

        ButterKnife.bind(this, rootView);

        CustomLayoutManager layoutManager = new CustomLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = detailsViewModel.getRecipe().getValue();
                if (recipe != null) {
                    if (ingredientsSelectorListener != null) {
                        ingredientsSelectorListener.onIngredientsSelected(recipe.getId());
                    }
                }
            }
        });

        return rootView;
    }

    public void setIngredientsSelectorListener(
            IngredientsSelectorListener ingredientsSelectorListener) {
        this.ingredientsSelectorListener = ingredientsSelectorListener;
    }

    /**
     * This RecyclerView LayoutManager helps with scrolling to a selected step.
     */
    static class CustomLayoutManager extends LinearLayoutManager {
        CustomLayoutManager(Context context) {
            super(context, RecyclerView.VERTICAL, false);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position) {
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override
                protected int getVerticalSnapPreference() {
                    return SNAP_TO_START;
                }

                @Nullable
                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return CustomLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                }
            };
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }

    private void loadRecipe() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(IntentArgs.ARG_RECIPE_ID)) {
            int recipeId = arguments.getInt(IntentArgs.ARG_RECIPE_ID);
            if (detailsViewModel.getRecipe().getValue() == null ||
                recipeId != detailsViewModel.getRecipe().getValue().getId()) {
                detailsViewModel.loadRecipe(recipeId);
            }
        }
    }

    private void scrollToSelectedStep() {
        final int stepIndex = detailsViewModel.getStepIndex();
        if (viewAdapter != null && IntentArgs.isStepSelected(stepIndex)) {
            Timber.d("Scrolling to step index: %d", stepIndex);
            recyclerView.smoothScrollToPosition(stepIndex);
        }
    }

    private void initRecyclerViewAdapter(Recipe recipe) {
        viewAdapter = new RecipeDetailsViewAdapter(recipe, itemDetailsSelectorListener);
        recyclerView.setAdapter(viewAdapter);
    }

}