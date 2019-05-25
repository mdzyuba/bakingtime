package com.mdzyuba.bakingtime.view.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeDetailsViewAdapter extends RecyclerView.Adapter<RecipeDetailsViewAdapter.StepViewHolder> {

    private final Recipe recipe;
    private final RecipeStepSelectorListener itemDetailsSelectorListener;
    private int selectedStepPk;
    private int previouslySelectedItemIndex = IntentArgs.STEP_NOT_SELECTED;

    public RecipeDetailsViewAdapter(Recipe recipe, @Nullable
            RecipeStepSelectorListener itemDetailsSelectorListener) {
        this.recipe = recipe;
        this.itemDetailsSelectorListener = itemDetailsSelectorListener;
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Step step = (Step) view.getTag();
            if (itemDetailsSelectorListener != null) {
                itemDetailsSelectorListener.onStepSelected(step);
            }
        }
    };

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
        holder.bind(step, selectedStepPk);
        holder.itemView.setTag(step);
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        List<Step> steps = recipe.getSteps();
        return steps != null ? steps.size() : 0;
    }

    // TODO: change to the step index. Also add a scroll to the selected item.
    public void setSelectedStepPk(int selectedStepPk) {
        this.selectedStepPk = selectedStepPk;
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            Step step = recipe.getSteps().get(i);
            if (step.getPk() == selectedStepPk) {
                if (previouslySelectedItemIndex != i) {
                    notifyItemChanged(previouslySelectedItemIndex);
                    previouslySelectedItemIndex = i;
                }
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_step_id)
        TextView stepId;

        @BindView(R.id.tv_name)
        TextView name;

        private final Context context;

        StepViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = itemView.getContext();
        }

        void bind(Step step, int selectedStepPk) {
            if (step.getId() > 0) {
                stepId.setText(context.getString(R.string.number_format, step.getId()));
            } else {
                stepId.setText("");
            }
            name.setText(step.getShortDescription());
            if (step.getPk() == selectedStepPk) {
                itemView.setSelected(true);
            } else {
                itemView.setSelected(false);
            }
        }
    }
}
