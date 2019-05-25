package com.mdzyuba.bakingtime.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import timber.log.Timber;

public class IngredientsListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.d("onGetViewFactory");
        return new IngredientsListRemoteViewFactory(this.getApplicationContext());
    }

}
