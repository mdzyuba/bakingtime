package com.mdzyuba.bakingtime.view.ingredients;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.model.Ingredient;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.view.IntentArgs;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;


public class IngredientsListFragment extends Fragment {

    private IngredientsListViewModel viewModel;

    @BindView(R.id.ingredients_list)
    ListView ingredientsList;

    @BindView(R.id.tv_servings)
    TextView tvServings;

    private Unbinder unbinder;

    private IngredientsListAdapter listAdapter;

    private final Observer<Recipe> recipeIsReady = new Observer<Recipe>() {
        @Override
        public void onChanged(Recipe recipe) {
            ArrayList<Ingredient> ingredientList = new ArrayList<>(recipe.getIngredients());
            listAdapter.updateIngredients(ingredientList);
            tvServings.setText(getString(R.string.servings, recipe.getServings()));
        }
    };

    public static IngredientsListFragment newInstance() {
        return new IngredientsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ingredients_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        Context context = getContext();
        if (context == null) {
            Timber.e("The context is null. Unable to create the fragment.");
            return rootView;
        }
        listAdapter = new IngredientsListAdapter(context, new ArrayList<>());
        ingredientsList.setAdapter(listAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IngredientsListViewModel.class);
        viewModel.getRecipe().observe(getViewLifecycleOwner(), recipeIsReady);
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
        if (arguments == null) {
            Timber.e("The fragment arguments is null. Unable to load a recipe.");
            return;
        }
        if (!arguments.containsKey(IntentArgs.ARG_RECIPE_ID)) {
            Timber.e("The recipeId must be provided. Unable to load a recipe.");
            return;
        }
        int recipeId = arguments.getInt(IntentArgs.ARG_RECIPE_ID);
        Timber.d("loading recipe: %s", recipeId);
        viewModel.loadRecipe(recipeId);
    }
}
