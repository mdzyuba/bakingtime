package com.mdzyuba.bakingtime.view.step;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.databinding.StepFragmentBinding;
import com.mdzyuba.bakingtime.http.HttpClientProvider;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.IntentArgs;
import com.mdzyuba.bakingtime.view.details.ErrorDialog;
import com.mdzyuba.bakingtime.view.details.RecipeDetailsViewModel;
import com.mdzyuba.bakingtime.view.details.RecipeStepSelectorListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * The StepFragment provides Step details.
 *
 * It is reloaded for each step. The fragment instance is not reused for more than one step.
 */
public class StepFragment extends Fragment {
    /**
     * Enables playing only in the landscape mode for the Master Detail flow case.
     */
    public static final String ARG_LANDSCAPE_ONLY = "landscape_only";

    private static final String TAG = StepFragment.class.getSimpleName();
    private RecipeDetailsViewModel detailsViewModel;
    private RecipeStepSelectorListener itemDetailsSelectorListener;
    private PlaybackStateCompat.Builder stateBuilder;
    private StepFragmentBinding viewBinding;
    private PlayerHelper playerHelper;
    private View rootView;
    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.button_next)
    ImageButton nextButton;

    @Nullable
    @BindView(R.id.button_prev)
    ImageButton prevButton;

    @BindView(R.id.video_player)
    PlayerView playerView;

    @Nullable
    @BindView(R.id.guideline)
    Guideline guideline;

    @Nullable
    @BindView(R.id.tv_description)
    TextView description;

    @BindView(R.id.step_details_page)
    ConstraintLayout constraintLayout;

    private final GestureDetector.SimpleOnGestureListener gestureListener =
            new GestureDetector.SimpleOnGestureListener() {

        private static final int SWIPE = 50;
        private static final int VELOCITY = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            boolean result = false;
            if (Math.abs(diffY) > SWIPE || Math.abs(velocityY) > VELOCITY) {
                if (diffY > 0) {
                    // swipe right
                    onNextStepClick();
                } else {
                    // swipe left
                    onPreviousStepClick();
                }
                result = true;
            }
            return result;
        }
    };

    private View.OnTouchListener onTouchListener;

    private final Observer<Recipe> recipeObserver = new Observer<Recipe>() {
        @Override
        public void onChanged(Recipe recipe) {
            detailsViewModel.getRecipe().removeObserver(this);
            Bundle arguments = getArguments();
            if (arguments == null) {
                Timber.e("The fragment arguments are null.");
                return;
            }
            int stepIndex = IntentArgs.getSelectedStep(arguments);
            int currentStepIndex = detailsViewModel.getStepIndex();
            if (currentStepIndex != stepIndex) {
                detailsViewModel.selectStep(stepIndex);
            }
        }
    };

    private final Observer<Step> stepObserver = new Observer<Step>() {
        @Override
        public void onChanged(Step step) {
            detailsViewModel.getStep().removeObserver(this);
            if (step == null) {
                return;
            }
            String videoURL = step.getVideoURL();
            if (TextUtils.isEmpty(videoURL)) {
                Timber.d("No videoUrl provided");
                hidePlayer();
                return;
            }
            playerHelper.setVideoURL(videoURL);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("The activity is null.");
            return;
        }
        playerHelper = new PlayerHelper();

        final GestureDetectorCompat gestureDetector =
                new GestureDetectorCompat(getContext(), gestureListener);
        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        break;
                    default:
                        break;
                }
                return gestureDetector.onTouchEvent(event);
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil
                .inflate(inflater, R.layout.step_fragment, container, false);
        rootView = viewBinding.getRoot();
        unbinder = ButterKnife.bind(this, rootView);
        viewBinding.setLifecycleOwner(getViewLifecycleOwner());

        if (nextButton != null && prevButton != null) {
            nextButton.setOnClickListener(v -> onNextStepClick());
            prevButton.setOnClickListener(v -> onPreviousStepClick());
        }

        if (isLandscapeOrientation()) {
            playerView.setOnTouchListener(onTouchListener);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        detailsViewModel = ViewModelProviders.of(activity).get(RecipeDetailsViewModel.class);
        detailsViewModel.getRecipe().observe(getViewLifecycleOwner(), recipeObserver);
        detailsViewModel.getStep().observe(getViewLifecycleOwner(), stepObserver);

        playerHelper.restorePlayerPosition(savedInstanceState);
        viewBinding.setViewModel(detailsViewModel);
        if (activity instanceof RecipeStepSelectorListener) {
            this.itemDetailsSelectorListener = (RecipeStepSelectorListener) activity;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        playerHelper.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        playerHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        playerHelper.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        playerHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        playerHelper.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerHelper.onDestroy();
        itemDetailsSelectorListener = null;
        onTouchListener = null;
        viewBinding = null;
        detailsViewModel = null;
        rootView = null;
        playerHelper = null;
    }

    private void onNextStepClick() {
        Step step = detailsViewModel.getNextStep();
        showStep(step);
    }

    private void onPreviousStepClick() {
        Step step = detailsViewModel.getPrevStep();
        showStep(step);
    }

    private void showStep(Step step) {
        playerHelper.resetStartPosition();
        playerHelper.onStop();
        if (step != null && itemDetailsSelectorListener != null) {
            itemDetailsSelectorListener.onStepSelected(step);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void hidePlayer() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            playerView.setVisibility(View.GONE);
            if (guideline != null) {
                guideline.setVisibility(View.GONE);
                guideline.setGuidelinePercent(0f);
            }
            if (description != null && isLandscapeOrientation() &&
                description.getVisibility() == View.GONE) {
                description.setVisibility(View.VISIBLE);
                constraintLayout.setOnTouchListener(onTouchListener);
            }
        }
    }

    private boolean isLandscapeOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * The PlayerHelper class provides methods to simplify Exoplayer integration with the Fragment.
     *
     * Credits to the Udacity code reviewer and the ExoPlayer example:
     * https://github.com/google/ExoPlayer/blob/release-v2/demos/main/src/main/java/com/google/android/exoplayer2/demo/PlayerActivity.java
     */
    private class PlayerHelper {
        private static final String KEY_POSITION = "position";
        private static final String KEY_WINDOW = "window";

        private SimpleExoPlayer exoPlayer;
        private PlayerEventListener playerEventListener;
        private MediaSessionCompat mediaSession;
        private DefaultBandwidthMeter bandwidthMeter;
        private String videoURL;
        private int startWindow;
        private long startPosition;

        void onStart() {
            if (Util.SDK_INT > 23) {
                initializePlayer();
                if (playerView != null) {
                    playerView.onResume();
                }
            }
        }

        void onResume() {
            if (Util.SDK_INT <= 23 || exoPlayer == null) {
                initializePlayer();
                if (playerView != null) {
                    playerView.onResume();
                }
            }
            // Load the video and start playing.
            Bundle arguments = getArguments();
            if (exoPlayer == null || videoURL == null || arguments == null) {
                Timber.w("Unable to load the video");
                return;
            }
            boolean playInLandscapeOnly = arguments.getBoolean(ARG_LANDSCAPE_ONLY, false);
            boolean landscapeOrientation = isLandscapeOrientation();
            if (!playInLandscapeOnly || landscapeOrientation) {
                loadVideo(videoURL);
            }
        }

        void onPause() {
            if (Util.SDK_INT <= 23) {
                if (playerView != null) {
                    playerView.onPause();
                }
                releasePlayer();
            }
        }

        void onSaveInstanceState(@NonNull Bundle outState) {
            savePlayerPosition(outState);
        }

        void onStop() {
            if (Util.SDK_INT > 23) {
                if (playerView != null) {
                    playerView.onPause();
                }
                releasePlayer();
            }
        }

        void onDestroy() {
            if (mediaSession != null) {
                mediaSession.setActive(false);
            }
        }

        void setVideoURL(String videoURL) {
            this.videoURL = videoURL;
        }

        private void initializePlayer() {
            Context context = getContext();
            if (context == null) {
                Timber.e("Unable to init the player. The context is null");
                return;
            }
            bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
            exoPlayer = createPlayer(context);
            setupMediaSession();
            playerEventListener = new PlayerEventListener();
            exoPlayer.addListener(playerEventListener);
            playerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(true);
        }

        private SimpleExoPlayer createPlayer(Context context) {
            bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
            DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();
            DefaultLoadControl defaultLoadControl = new DefaultLoadControl();
            return ExoPlayerFactory
                    .newSimpleInstance(context, defaultTrackSelector, defaultLoadControl);
        }

        private void releasePlayer() {
            resetStartPosition();
            if (exoPlayer != null) {
                if (playerEventListener != null) {
                    exoPlayer.removeListener(playerEventListener);
                    playerEventListener = null;
                }
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;
            }
            if (mediaSession != null) {
                mediaSession.setActive(false);
                mediaSession = null;
            }
            if (bandwidthMeter != null) {
                bandwidthMeter = null;
            }
        }

        @Nullable
        private MediaSource buildMediaSource(Context context, @Nullable String uri) {
            if (TextUtils.isEmpty(uri)) {
                return null;
            }
            String userAgent = Util.getUserAgent(context, "ExoPlayer");
            OkHttpDataSourceFactory dataSourceFactory =
                    new OkHttpDataSourceFactory(HttpClientProvider.getClient(context), userAgent,
                                                bandwidthMeter);
            Uri videoUri = Uri.parse(uri);
            return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
        }

        private void setupMediaSession() {
            Context context = getContext();
            if (context == null) {
                Timber.e("The context is null. Unable to create a media session.");
                return;
            }
            mediaSession = new MediaSessionCompat(context, TAG);
            mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mediaSession.setMediaButtonReceiver(null);

            stateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackStateCompat.ACTION_PLAY_PAUSE);
            mediaSession.setPlaybackState(stateBuilder.build());
            mediaSession.setCallback(new PlayerSessionCallback());
        }

        private void loadVideo(String videoURL) {
            MediaSource mediaSource = buildMediaSource(getContext(), videoURL);
            if (mediaSource == null) {
                Timber.e("Unable to load the video");
                return;
            }
            mediaSession.setActive(true);
            exoPlayer.setPlayWhenReady(true);
            boolean haveStartPosition = startPosition != C.TIME_UNSET;
            if (haveStartPosition) {
                Timber.d("Resuming playback from %d", startPosition);
                exoPlayer.seekTo(startWindow, startPosition);
                exoPlayer.prepare(mediaSource, false, false);
            } else {
                Timber.d("Starting a new playback");
                exoPlayer.prepare(mediaSource, true, true);
            }
        }

        private void savePlayerPosition(@NonNull Bundle outState) {
            if (exoPlayer == null) {
                return;
            }
            startWindow = exoPlayer.getCurrentWindowIndex();
            startPosition = Math.max(0, exoPlayer.getContentPosition());
            Timber.d("Saving current position: %d", startPosition);
            outState.putLong(KEY_POSITION, startPosition);
            outState.putInt(KEY_WINDOW, startWindow);
        }

        private void restorePlayerPosition(@Nullable Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                startPosition = savedInstanceState.getLong(KEY_POSITION, C.TIME_UNSET);
                startWindow = savedInstanceState.getInt(KEY_WINDOW, C.INDEX_UNSET);
                Timber.d("Restoring player position: %d", startPosition);
            } else {
                resetStartPosition();
            }
        }

        private void resetStartPosition() {
            Timber.d("Resetting player position");
            startPosition = C.TIME_UNSET;
            startWindow = C.INDEX_UNSET;
        }

        private class PlayerEventListener implements Player.EventListener {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups,
                                        TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                if (isLoading && playerView != null) {
                    playerView.hideController();
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playerView == null || stateBuilder == null || mediaSession == null) {
                    return;
                }
                if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
                    stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                                          exoPlayer.getCurrentPosition(), 1f);
                    playerView.hideController();
                } else if((playbackState == ExoPlayer.STATE_READY)){
                    stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                                          exoPlayer.getCurrentPosition(), 1f);
                }
                mediaSession.setPlaybackState(stateBuilder.build());
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Timber.e(error,"Player error: %s", error.getMessage());
                hidePlayer();
                releasePlayer();
                Context context = getContext();
                if (context == null) {
                    return;
                }
                ErrorDialog.showErrorDialog(context, new ErrorDialog.Retry() {
                    @Override
                    public void retry() {
                        showStep(detailsViewModel.getStep().getValue());
                    }
                });
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {
            }
        }

        private class PlayerSessionCallback extends MediaSessionCompat.Callback {
            @Override
            public void onPlay() {
                exoPlayer.setPlayWhenReady(true);
            }

            @Override
            public void onPause() {
                exoPlayer.setPlayWhenReady(false);
            }

            @Override
            public void onSkipToPrevious() {
                exoPlayer.seekTo(0);
            }
        }
    }

}
