package com.mdzyuba.bakingtime.view.details;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.RecipeDetailActivity;
import com.mdzyuba.bakingtime.RecipeListActivity;
import com.mdzyuba.bakingtime.images.PicassoProvider;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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

    @BindView(R.id.image)
    ImageView imageView;

    @BindView(R.id.image_frame)
    FrameLayout imageFrame;

    @BindView(R.id.loading_progress)
    ProgressBar imageLoadingProgressBar;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;

    private Unbinder unbinder;

    private RecipeDetailsViewModel detailsViewModel;
    private RecipeStepSelectorListener itemDetailsSelectorListener;
    private RecipeDetailsViewAdapter viewAdapter;

    private final Observer<Recipe> recipeObserver = new Observer<Recipe>() {
        @Override
        public void onChanged(Recipe recipe) {
            if (viewAdapter == null) {
                initRecyclerViewAdapter(recipe);
            }
            if (IntentArgs.isStepSelected(getArguments())) {
                int stepIndex = IntentArgs.getSelectedStep(getArguments());
                Timber.d("Setting a step index %d", stepIndex);
                detailsViewModel.selectStep(stepIndex);
            }
            Context context = getContext();
            if (context != null && !TextUtils.isEmpty(recipe.getImage())) {
                loadRecipeImage(recipe, context);
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

    private final View.OnClickListener ingredientsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            detailsViewModel.selectIngredients();
        }
    };

    private final Observer<Boolean> ingredientsSelectorObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean state) {
            ingredients.setSelected(state);
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_details_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        ingredients.setOnClickListener(ingredientsClickListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.w("The activity is null. Unable to create the view model.");
            return;
        }
        detailsViewModel = ViewModelProviders.of(activity).get(RecipeDetailsViewModel.class);
        detailsViewModel.getRecipe().observe(getViewLifecycleOwner(), recipeObserver);
        detailsViewModel.getStep().observe(getViewLifecycleOwner(), stepObserver);
        detailsViewModel.getStepIndexLd().observe(getViewLifecycleOwner(), stepIndexObserver);
        detailsViewModel.ingredientsSelectorLd.observe(getViewLifecycleOwner(), ingredientsSelectorObserver);

        if (savedInstanceState == null) {
            loadRecipe();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

    private void loadRecipeImage(Recipe recipe, Context context) {
        imageFrame.setVisibility(View.VISIBLE);
        Picasso picasso = PicassoProvider.getPicasso(context);
        try {
            Uri imageUri = Uri.parse(recipe.getImage());
            Callback imageLoadingCallback = new Callback() {
                @Override
                public void onSuccess() {
                    imageLoadingProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    imageLoadingProgressBar.setVisibility(View.GONE);
                    imageFrame.setVisibility(View.GONE);
                    Timber.e(e, "Unable to load the image");
                }
            };
            picasso.load(imageUri)
                   .placeholder(R.drawable.image_placeholder)
                   .into(imageView, imageLoadingCallback);
        } catch (Exception e) {
            Timber.e(e, "Unable to load the image %s", recipe.getImage());
            ErrorDialog.showErrorDialog(context, new ErrorDialog.Retry() {
                @Override
                public void retry() {
                    loadRecipe();
                }
            });
        }
    }

    private void scrollToSelectedStep() {
        final int stepIndex = detailsViewModel.getStepIndex();
        if (viewAdapter != null && IntentArgs.isStepSelected(stepIndex)) {
            Timber.d("Scrolling to step index: %d", stepIndex);
            recyclerView.smoothScrollToPosition(stepIndex);

            RecyclerView.ViewHolder viewHolder =
                    recyclerView.findViewHolderForAdapterPosition(stepIndex);
            if (viewHolder != null) {
                nestedScrollView.requestChildFocus(recyclerView, viewHolder.itemView);
            }
        }
    }

    private void initRecyclerViewAdapter(Recipe recipe) {
        viewAdapter = new RecipeDetailsViewAdapter(recipe, itemDetailsSelectorListener);
        recyclerView.setAdapter(viewAdapter);
    }

}