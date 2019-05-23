package com.mdzyuba.bakingtime.view.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class IngredientsListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsListRemoteViewFactory(this.getApplicationContext());
    }
}
