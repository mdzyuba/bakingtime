package com.mdzyuba.bakingtime.utils;

import android.app.Activity;
import android.content.Context;

import com.mdzyuba.bakingtime.R;

import java.util.concurrent.CountDownLatch;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


public class TestUtil {

    public static boolean isDualFrameMode(Context context) {
        return context.getResources().getBoolean(R.bool.dual_pane_mode);
    }

    public static void delay() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public static void setOrientation(Activity activity, int newOrientation) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        activity.setRequestedOrientation(newOrientation);
        getInstrumentation().waitForIdle(new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
