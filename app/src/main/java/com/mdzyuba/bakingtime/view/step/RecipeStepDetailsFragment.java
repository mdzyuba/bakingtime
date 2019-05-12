package com.mdzyuba.bakingtime.view.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.databinding.RecipeStepDetailsFragmentBinding;
import com.mdzyuba.bakingtime.model.Step;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeStepDetailsFragment extends Fragment {

    public static final String ARG_RECIPE_STEP_ID = "recipeStepId";

    private RecipeStepDetailsViewModel recipeStepDetailsViewModel;

    private RecipeStepDetailsFragmentBinding viewBinding;

    private Integer stepPk;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipeStepDetailsViewModel = ViewModelProviders.of(this)
                                                       .get(RecipeStepDetailsViewModel.class);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_RECIPE_STEP_ID)) {
            stepPk = arguments.getInt(ARG_RECIPE_STEP_ID);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil
                .inflate(inflater, R.layout.recipe_step_details_fragment, container, false);
        View rootView = viewBinding.getRoot();
        ButterKnife.bind(this, rootView);
        viewBinding.setLifecycleOwner(this);
        viewBinding.setViewModel(recipeStepDetailsViewModel);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recipeStepDetailsViewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);

        recipeStepDetailsViewModel.getStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(Step step) {
                Timber.d("step: %s", step);
            }
        });
        if (stepPk != null) {
            recipeStepDetailsViewModel.loadStep(stepPk);
        }
    }

}
