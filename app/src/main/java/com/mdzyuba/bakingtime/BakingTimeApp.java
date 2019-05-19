package com.mdzyuba.bakingtime;

import android.app.Application;
import android.os.StrictMode;

import timber.log.Timber;

public class BakingTimeApp extends Application {
    private static final boolean DEV_MODE = BuildConfig.DEV_MODE;

    @Override
    public void onCreate() {
        enableStrictMode();
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    /**
     * https://developer.android.com/reference/android/os/StrictMode
     */
    private void enableStrictMode() {
        if (DEV_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                               .detectDiskReads()
                                               .detectDiskWrites()
                                               .detectAll()
                                               .penaltyLog()
                                               .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                                           .detectLeakedSqlLiteObjects()
                                           .detectLeakedClosableObjects()
                                           .penaltyLog()
                                           .penaltyDeath()
                                           .build());
        }
    }
}
