package com.mdzyuba.bakingtime.view.step;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.model.Step;

public class RecipeStepDetailsFragment extends Fragment {

    public static final String ARG_RECIPE_STEP_ID = "recipeStepId";

    private RecipeStepDetailsViewModel recipeStepDetailsViewModel;

    private Integer stepPk;

    @BindView(R.id.tv_label)
    TextView tvLabel;

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
        View rootView = inflater.inflate(R.layout.recipe_step_details_fragment, container, false);
        ButterKnife.bind(this, rootView);

        tvLabel.setText("Step pk: " + stepPk);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recipeStepDetailsViewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);

        recipeStepDetailsViewModel.getStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(Step step) {
                Timber.d("step: " + step);
            }
        });
        if (stepPk != null) {
            recipeStepDetailsViewModel.loadStep(stepPk);
        }
    }

}
