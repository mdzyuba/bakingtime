package com.mdzyuba.bakingtime.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeDetailsViewAdapter extends RecyclerView.Adapter<RecipeDetailsViewAdapter.StepViewHolder> {

    private final Recipe recipe;

    public RecipeDetailsViewAdapter(Recipe recipe) {
        this.recipe = recipe;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final boolean shouldAttachToParentImmediately = false;
        View view = layoutInflater
                .inflate(R.layout.recipe_details_item, parent, shouldAttachToParentImmediately);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        List<Step> steps = recipe.getSteps();
        Step step = steps.get(position);
        holder.bind(step);
    }

    @Override
    public int getItemCount() {
        List<Step> steps = recipe.getSteps();
        return steps.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_step_id)
        TextView stepId;

        @BindView(R.id.tv_name)
        TextView name;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Step step) {
            if (step.getId() > 0) {
                stepId.setText(String.format("%d.", step.getId()));
            }
            name.setText(step.getShortDescription());
        }
    }
}
