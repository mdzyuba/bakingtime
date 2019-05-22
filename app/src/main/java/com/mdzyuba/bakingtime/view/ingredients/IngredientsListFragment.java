package com.mdzyuba.bakingtime.view.ingredients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import timber.log.Timber;


public class IngredientsListFragment extends Fragment {

    private IngredientsListViewModel viewModel;

    @BindView(R.id.ingredients_list)
    ListView ingredientsList;

    IngredientsListAdapter listAdapter;

    public static IngredientsListFragment newInstance() {
        return new IngredientsListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        listAdapter = new IngredientsListAdapter(getContext(), new ArrayList<>());
        viewModel = ViewModelProviders.of(this).get(IngredientsListViewModel.class);
        viewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                Timber.d("recipe is ready");
                ArrayList<Ingredient> ingredientList = new ArrayList<>(recipe.getIngredients());
                listAdapter.updateIngredients(ingredientList);
            }
        });
        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(IntentArgs.ARG_RECIPE_ID)) {
                int recipeId = arguments.getInt(IntentArgs.ARG_RECIPE_ID);
                Timber.d("loading recipe: %s", recipeId);
                viewModel.loadRecipe(recipeId);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View rootView = inflater.inflate(R.layout.ingredients_list_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ingredientsList.setAdapter(listAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("onActivityCreated");
    }

}
