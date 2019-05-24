package com.mdzyuba.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mdzyuba.bakingtime.model.Ingredient;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.LoadRecipeTask;
import com.mdzyuba.bakingtime.view.ingredients.IngredientsViewUtil;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import timber.log.Timber;

public class IngredientsListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private final ArrayList<Ingredient> ingredientList;
    private final Context context;
    private Recipe recipe;

    public IngredientsListRemoteViewFactory(Context context) {
        this.context = context;
        ingredientList = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        // TODO: change it to a right value
        loadRecipe(1);
    }

    @Override
    public void onDataSetChanged() {
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

    private void loadRecipe(@NonNull Integer recipeId) {
        Timber.d("Load recipe: %d", recipeId);
        MutableLiveData<Recipe> recipe = new MutableLiveData<>();
        recipe.observeForever(new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                IngredientsListRemoteViewFactory.this.recipe = recipe;
                notifyDataChanged();
            }

            private void notifyDataChanged() {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, BakingTimeWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredients_list);
            }
        });
        LoadRecipeTask loadRecipeTask = new LoadRecipeTask(context, recipeId, recipe);
        loadRecipeTask.execute();
    }
}
