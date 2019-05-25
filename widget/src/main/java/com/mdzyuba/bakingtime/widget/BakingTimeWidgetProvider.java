package com.mdzyuba.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.repository.LoadRecipeTask;
import com.mdzyuba.bakingtime.view.IntentArgs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import timber.log.Timber;

/**
 * Implements BakingTime Widget Provider.
 *
 * The widget displays the recipe name and ingredients.
 *
 * The recipeId is set by the UPDATE_RECIPE broadcast and IntentArgs.ARG_RECIPE_ID parameter.
 */
public class BakingTimeWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_RECIPE = "com.mdzyuba.bakingtime.widget.UPDATE_RECIPE";
    private static final int NO_RECIPE_SELECTED = -1;

    // TODO: find a better way to pass the recipeId to IngredientsListRemoteViewFactory.
    static Recipe recipe;

    private int recipeId = NO_RECIPE_SELECTED;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Timber.d("updateAppWidget %d", appWidgetId);
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                                            R.layout.baking_time_widget_provider);
        initWidgetView(context, widgetView);

        appWidgetManager.updateAppWidget(appWidgetId, widgetView);
    }

    private void initWidgetView(Context context, RemoteViews widgetView) {
        PendingIntent pendingIntent = getPendingIntent(context);

        if (recipe == null) {
            widgetView.setTextViewText(R.id.tv_recipe_title, "Baking Ingredients");
            widgetView.setViewVisibility(R.id.tv_no_recipe_selected, View.VISIBLE);
            widgetView.setViewVisibility(R.id.ingredients_list, View.GONE);
            widgetView.setOnClickPendingIntent(R.id.tv_no_recipe_selected, pendingIntent);
            return;
        }

        widgetView.setViewVisibility(R.id.tv_no_recipe_selected, View.GONE);
        widgetView.setViewVisibility(R.id.ingredients_list, View.VISIBLE);

        widgetView.setTextViewText(R.id.tv_recipe_title, recipe.getName());

        // Launch the app on the title click.
        widgetView.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Launch the app on the list items click.
        // This intent will be updated by the fill intent when a user clicks on the list items.
        Intent intentTemplate = new Intent(context, BakingTimeWidgetProvider.class);
        PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context,
                                                                         0,
                                                                         intentTemplate,
                                                                         PendingIntent.FLAG_UPDATE_CURRENT);
        widgetView.setPendingIntentTemplate(R.id.ingredients_list, pendingIntentTemplate);

        Intent intent = new Intent(context, IngredientsListService.class);
        intent.putExtra(IntentArgs.ARG_RECIPE_ID, recipeId);
        Timber.d("recipeId: %d", recipeId);
        widgetView.setRemoteAdapter(R.id.ingredients_list, intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Timber.d("onReceive: %s", action);

        if (action == null) {
            // Launch the app if the broadcast is initiated by the widget ListView items.
            Intent appLaunchIntent = getAppLaunchIntent(context);
            context.startActivity(appLaunchIntent);
        } else if (UPDATE_RECIPE.equals(action)){
            // This broadcast is sent once a user selects a recipe.
            recipeId = intent.getIntExtra(IntentArgs.ARG_RECIPE_ID, NO_RECIPE_SELECTED);
            if (recipeId != NO_RECIPE_SELECTED) {
                loadRecipe(context, recipeId);
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Timber.d("onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Timber.d("onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }

    private Intent getAppLaunchIntent(Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent launchIntent = getAppLaunchIntent(context);
        return PendingIntent
                .getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void loadRecipe(Context context, int recipeId) {
        Timber.d("Load recipe: %d", recipeId);
        MutableLiveData<Recipe> recipe = new MutableLiveData<>();
        recipe.observeForever(new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                BakingTimeWidgetProvider.recipe = recipe;
                notifyDataChanged();
            }

            private void notifyDataChanged() {
                Recipe recipeValue = recipe.getValue();
                if (recipeValue == null) {
                    Timber.e("Unable to load a recipe");
                    return;
                }
                Timber.d("recipe is loaded: %d", recipeValue.getId());
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, BakingTimeWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
                Timber.d("notifyAppWidgetViewDataChanged");
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredients_list);

                RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                                                         R.layout.baking_time_widget_provider);
                initWidgetView(context, widgetView);
                appWidgetManager.updateAppWidget(appWidgetIds, widgetView);
            }
        });
        LoadRecipeTask loadRecipeTask = new LoadRecipeTask(context, recipeId, recipe);
        loadRecipeTask.execute();
    }
}

