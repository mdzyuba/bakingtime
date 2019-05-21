package com.mdzyuba.bakingtime.view.step;

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

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mdzyuba.bakingtime.R;
import com.mdzyuba.bakingtime.databinding.RecipeStepDetailsFragmentBinding;
import com.mdzyuba.bakingtime.model.Recipe;
import com.mdzyuba.bakingtime.model.Step;
import com.mdzyuba.bakingtime.view.details.RecipeDetailFragment;
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
import timber.log.Timber;

public class RecipeStepDetailsFragment extends Fragment {
    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();

    private RecipeDetailsViewModel detailsViewModel;

    private PlayerEventListener playerEventListener;

    private RecipeStepSelectorListener itemDetailsSelectorListener;

    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

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
            Timber.d("diff %f velocity y: %f", diffY, velocityY);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Timber.e("The activity is null.");
            return;
        }
        initializePlayer();

        detailsViewModel = ViewModelProviders.of(activity).get(RecipeDetailsViewModel.class);

        detailsViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                detailsViewModel.getRecipe().removeObserver(this);
                Bundle arguments = getArguments();
                if (arguments == null) {
                    Timber.e("The fragment arguments are null.");
                    return;
                }
                int stepIndex = arguments.getInt(RecipeDetailFragment.ARG_STEP_INDEX, 0);
                Step step = detailsViewModel.getStep().getValue();
                Timber.d("step index: %d, model step: %s", stepIndex, step);
                int currentStepIndex = detailsViewModel.getStepIndex(step);
                if (step != null && currentStepIndex != stepIndex) {
                    detailsViewModel.setStepIndex(stepIndex);
                }
            }
        });

        detailsViewModel.getStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(Step step) {
                detailsViewModel.getStep().removeObserver(this);
                Timber.d("step changed - initializePlayer: %s", step);
                String videoURL = step.getVideoURL();
                if (TextUtils.isEmpty(videoURL)) {
                    hidePlayer();
                    return;
                }
                MediaSource mediaSource = buildMediaSource(getContext(), videoURL);
                mediaSession.setActive(true);
                exoPlayer.prepare(mediaSource);
            }
        });

        final GestureDetectorCompat gestureDetector =
                new GestureDetectorCompat(getContext(), gestureListener);
        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return activity.onTouchEvent(event);
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RecipeStepDetailsFragmentBinding viewBinding = DataBindingUtil
                .inflate(inflater, R.layout.recipe_step_details_fragment, container, false);
        View rootView = viewBinding.getRoot();
        ButterKnife.bind(this, rootView);
        viewBinding.setLifecycleOwner(this);
        viewBinding.setViewModel(detailsViewModel);

        playerView.setPlayer(exoPlayer);

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
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
        if (step != null && itemDetailsSelectorListener != null) {
            itemDetailsSelectorListener.onStepSelected(step);
        }
    }

    private void initializePlayer() {
        Timber.d("init player");
        Context context = getContext();
        PlayerProvider activity = (PlayerProvider) getActivity();
        if (activity == null || context == null) {
            return;
        }
        exoPlayer = activity.getPlayer();
        setupMediaSession();
        playerEventListener = new PlayerEventListener();
        exoPlayer.addListener(playerEventListener);
        exoPlayer.setPlayWhenReady(true);
    }

    @Nullable
    private MediaSource buildMediaSource(Context context, @Nullable String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        String userAgent = Util.getUserAgent(context, "ExoPlayer");
        DefaultDataSourceFactory dataSourceFactory =
                new DefaultDataSourceFactory(context, userAgent);

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

    private void hidePlayer() {
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

    private boolean isLandscapeOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
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
            Timber.d("Loading %s", isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                                       exoPlayer.getCurrentPosition(), 1f);
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
            PlayerProvider activity = (PlayerProvider) getActivity();
            if (activity != null) {
                SimpleExoPlayer exoPlayer = activity.getPlayer();
                exoPlayer.removeListener(playerEventListener);
            }
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

    public void setItemDetailsSelectorListener(
            RecipeStepSelectorListener itemDetailsSelectorListener) {
        this.itemDetailsSelectorListener = itemDetailsSelectorListener;
    }

    public interface PlayerProvider {
        SimpleExoPlayer getPlayer();
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
