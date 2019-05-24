package com.mdzyuba.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeWidgetProvider extends AppWidgetProvider {

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Timber.d("updateAppWidget %d", appWidgetId);
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                                            R.layout.baking_time_widget_provider);
        initWidgetView(context, widgetView);

        Intent intent = new Intent(context, IngredientsListService.class);
        widgetView.setRemoteAdapter(R.id.ingredients_list, intent);
        appWidgetManager.updateAppWidget(appWidgetId, widgetView);
    }

    private void initWidgetView(Context context, RemoteViews widgetView) {
        widgetView.setTextViewText(R.id.tv_recipe_title, "Baking Ingredients");

        // Launch the app on the title click.
        PendingIntent pendingIntent = getPendingIntent(context);
        widgetView.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Launch the app on the list items click.
        // This intent will be updated by the fill intent when a user clicks on the list items.
        Intent intentTemplate = new Intent(context, BakingTimeWidgetProvider.class);
        PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context,
                                                                         0,
                                                                         intentTemplate,
                                                                         PendingIntent.FLAG_UPDATE_CURRENT);
        widgetView.setPendingIntentTemplate(R.id.ingredients_list, pendingIntentTemplate);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive: %s", intent.getAction());
        if (intent.getAction() == null) {
            // Launch the app if the broadcast is initiated by the widget ListView items.
            Intent appLaunchIntent = getAppLaunchIntent(context);
            context.startActivity(appLaunchIntent);
        }
        super.onReceive(context, intent);
    }

    private Intent getAppLaunchIntent(Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent launchIntent = getAppLaunchIntent(context);
        return PendingIntent
                .getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

