package com.mdzyuba.bakingtime.view.step;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.RecipeStepDetailsActivity;
import com.mdzyuba.bakingtime.databinding.RecipeStepDetailsFragmentBinding;
import com.mdzyuba.bakingtime.model.Step;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeStepDetailsFragment extends Fragment {

    public static final String ARG_RECIPE_STEP_ID = "stepId";

    private RecipeStepDetailsViewModel recipeStepDetailsViewModel;

    @BindView(R.id.button_next)
    Button nextButton;

    @BindView(R.id.button_prev)
    Button prevButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeStepDetailsViewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RecipeStepDetailsFragmentBinding viewBinding = DataBindingUtil
                .inflate(inflater, R.layout.recipe_step_details_fragment, container, false);
        View rootView = viewBinding.getRoot();
        ButterKnife.bind(this, rootView);
        viewBinding.setLifecycleOwner(this);
        viewBinding.setViewModel(recipeStepDetailsViewModel);
        nextButton.setOnClickListener(v -> onNextStepClick());
        prevButton.setOnClickListener(v -> onPreviousStepClick());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        recipeStepDetailsViewModel.step.observe(this, step -> Timber.d("step: %s", step));
        if (arguments != null && arguments.containsKey(ARG_RECIPE_STEP_ID)) {
            int stepPk = arguments.getInt(ARG_RECIPE_STEP_ID);
            recipeStepDetailsViewModel.loadStep(stepPk);
        }
    }

    private void onNextStepClick() {
        Step step = recipeStepDetailsViewModel.getNextStep();
        showStep(step);
    }

    private void onPreviousStepClick() {
        Step step = recipeStepDetailsViewModel.getPrevStep();
        showStep(step);
    }

    private void showStep(Step step) {
        if (step != null) {
            RecipeStepDetailsActivity.startActivity(getContext(), step);
        }
    }
}
