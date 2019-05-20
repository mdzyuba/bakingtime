package com.mdzyuba.bakingtime.view.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.RecipeListActivity;
import com.mdzyuba.bakingtime.model.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes;
    private final RecipeSelectorListener recipeSelectorListener;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe recipe = (Recipe) view.getTag();
            recipeSelectorListener.onRecipeSelected(recipe);
        }
    };

    public RecipeRecyclerViewAdapter(RecipeListActivity mParentActivity, List<Recipe> recipes) {
        this.recipes = recipes;
        this.recipeSelectorListener = mParentActivity;
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
