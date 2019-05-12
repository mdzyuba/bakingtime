package com.mdzyuba.bakingtime;

import android.app.Application;

import timber.log.Timber;

public class BakingTimeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
