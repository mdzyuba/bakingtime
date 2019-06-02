package com.mdzyuba.bakingtime.view.step;

import android.content.Context;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import timber.log.Timber;

/**
 * This class helps keeping a single instance of a player per an activity.
 *
 * https://medium.com/google-exoplayer/improved-decoder-reuse-in-exoplayer-ef4c6d99591d
 */
public class VideoPlayerSingleton {

    private static VideoPlayerSingleton sInstance;

    private SimpleExoPlayer exoPlayer;

    private VideoPlayerSingleton(Context context) {
        exoPlayer = createPlayer(context);
    }

    public synchronized static VideoPlayerSingleton getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new VideoPlayerSingleton(context);
        }
        return sInstance;
    }

    private SimpleExoPlayer createPlayer(Context context) {
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl();
        return ExoPlayerFactory
                .newSimpleInstance(context, defaultTrackSelector, defaultLoadControl);
    }

    public SimpleExoPlayer getExoPlayer(Context context) {
        if (exoPlayer == null) {
            exoPlayer = createPlayer(context);
        }
        return exoPlayer;
    }

    public void releasePlayer() {
        if (exoPlayer != null) {
            Timber.d("releasing the player");
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
