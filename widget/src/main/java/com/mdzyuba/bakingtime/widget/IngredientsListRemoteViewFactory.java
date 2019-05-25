package com.mdzyuba.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mdzyuba.bakingtime.model.Ingredient;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.view.ingredients.IngredientsViewUtil;

import java.util.ArrayList;

public class IngredientsListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private final ArrayList<Ingredient> ingredientList;
    private final Context context;

    public IngredientsListRemoteViewFactory(Context context) {
        this.context = context;
        ingredientList = new ArrayList<>();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        Recipe recipe = BakingTimeWidgetProvider.recipe;
        if (recipe != null) {
            ingredientList.clear();
            ingredientList.addAll(recipe.getIngredients());
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Ingredient ingredient = ingredientList.get(position);
        RemoteViews listItem = new RemoteViews(context.getPackageName(),
                                            R.layout.ingredients_list_item);
        listItem.setTextViewText(R.id.tv_ingredient, ingredient.getIngredient());
        listItem.setTextViewText(R.id.tv_quantity, IngredientsViewUtil
                .formatQuantity(context, ingredient.getQuantity()));
        listItem.setTextViewText(R.id.tv_measure,
                                 IngredientsViewUtil.formatMeasure(ingredient.getMeasure()));
        Intent fillInIntent = new Intent();
        listItem.setOnClickFillInIntent(R.id.tv_ingredient, fillInIntent);
        listItem.setOnClickFillInIntent(R.id.tv_quantity, fillInIntent);
        listItem.setOnClickFillInIntent(R.id.tv_measure, fillInIntent);
        return listItem;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // all items are the same
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO: revisit that
        return true;
    }

}
