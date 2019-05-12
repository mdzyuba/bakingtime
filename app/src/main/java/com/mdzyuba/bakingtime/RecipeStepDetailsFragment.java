package com.mdzyuba.bakingtime;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdzyuba.bakingtime.view.RecipeStepDetailsViewModel;

public class RecipeStepDetailsFragment extends Fragment {

    private RecipeStepDetailsViewModel mViewModel;

    public static RecipeStepDetailsFragment newInstance() {
        return new RecipeStepDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe_step_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);
        // TODO: Use the ViewModel
    }

}
