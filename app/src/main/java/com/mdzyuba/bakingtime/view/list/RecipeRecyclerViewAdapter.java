package com.mdzyuba.bakingtime.view.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.RecipeDetailActivity;
import com.mdzyuba.bakingtime.view.details.RecipeDetailFragment;
import com.mdzyuba.bakingtime.RecipeListActivity;
import com.mdzyuba.bakingtime.model.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder> {

    private final RecipeListActivity mParentActivity;
    private final List<Recipe> recipes;
    private final boolean twoPane;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe recipe = (Recipe) view.getTag();
            if (twoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(RecipeDetailFragment.ARG_RECIPE_ID, recipe.getId());
                arguments.putString(RecipeDetailFragment.ARG_RECIPE_NAME, recipe.getName());
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                               .add(R.id.item_detail_container, fragment).commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailFragment.ARG_RECIPE_ID, recipe.getId());
                intent.putExtra(RecipeDetailFragment.ARG_RECIPE_NAME, recipe.getName());
                context.startActivity(intent);
            }
        }
    };

    public RecipeRecyclerViewAdapter(RecipeListActivity mParentActivity, List<Recipe> recipes,
                                     boolean mTwoPane) {
        this.mParentActivity = mParentActivity;
        this.recipes = recipes;
        this.twoPane = mTwoPane;
    }

    @NonNull
    @Override
    public RecipeRecyclerViewAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.recipe_list_content, parent, false);
        return new RecipeRecyclerViewAdapter.RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.mIdView.setText(String.valueOf(recipe.getId()));
        holder.mContentView.setText(recipe.getName());

        holder.itemView.setTag(recipe);
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_text)
        TextView mIdView;

        @BindView(R.id.content)
        TextView mContentView;

        RecipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
