package com.mdzyuba.bakingtime.view.ingredients;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.model.Ingredient;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;


public class IngredientsListAdapter extends ArrayAdapter<Ingredient> {

    private final Context context;
    private final ArrayList<Ingredient> ingredientList;
    private final LayoutInflater layoutInflater;

    public IngredientsListAdapter(@NonNull Context context, ArrayList<Ingredient> ingredientList) {
        super(context, -1, ingredientList);
        this.context = context;
        this.ingredientList = ingredientList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateIngredients(ArrayList<Ingredient> values) {
        ingredientList.clear();
        ingredientList.addAll(values);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.ingredients_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Ingredient ingredient = ingredientList.get(position);
        viewHolder.init(context, ingredient);
        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.tv_ingredient)
        TextView tvIngredient;

        @BindView(R.id.tv_quantity)
        TextView tvQuantity;

        @BindView(R.id.tv_measure)
        TextView tvMeasure;

        ViewHolder(View rootView) {
            ButterKnife.bind(this, rootView);
        }

        void init(Context context, Ingredient ingredient) {
            tvIngredient.setText(ingredient.getIngredient());
            tvQuantity.setText(IngredientsViewUtil.formatQuantity(context, ingredient.getQuantity()));
            tvMeasure.setText(IngredientsViewUtil.formatMeasure(ingredient.getMeasure()));
        }


    }
}
