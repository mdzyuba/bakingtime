package com.mdzyuba.bakingtime.view.step;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
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
import com.mdzyuba.bakingtime.RecipeStepDetailsActivity;
import com.mdzyuba.bakingtime.databinding.RecipeStepDetailsFragmentBinding;
import com.mdzyuba.bakingtime.model.Step;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeStepDetailsFragment extends Fragment {

    public static final String ARG_RECIPE_STEP_ID = "stepId";
    public static final String ARG_RECIPE_STEP_NAME = "stepName";

    private RecipeStepDetailsViewModel recipeStepDetailsViewModel;

    private PlayerEventListener playerEventListener;

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

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

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
        recipeStepDetailsViewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);
        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    return true;
                }
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
        viewBinding.setViewModel(recipeStepDetailsViewModel);
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
        Bundle arguments = getArguments();
        recipeStepDetailsViewModel.step.observe(this, step -> {
            Timber.d("step: %s", step);
            initializePlayer(step.getVideoURL());
        });
        if (arguments != null && arguments.containsKey(ARG_RECIPE_STEP_ID)) {
            int stepPk = arguments.getInt(ARG_RECIPE_STEP_ID);
            recipeStepDetailsViewModel.loadStep(stepPk);
        }
    }

    private void onNextStepClick() {
        Step step = recipeStepDetailsViewModel.getNextStep();
        showStep(step);
    }

    private void onPreviousStepClick() {
        Step step = recipeStepDetailsViewModel.getPrevStep();
        showStep(step);
    }

    private void showStep(Step step) {
        if (step != null) {
            RecipeStepDetailsActivity.startActivity(getContext(), step);
        }
    }

    private void initializePlayer(String uri) {
        if (TextUtils.isEmpty(uri)) {
            hidePlayer();
            return;
        }
        Context context = getContext();
        PlayerProvider activity = (PlayerProvider) getActivity();
        if (activity == null || context == null) {
            return;
        }
        SimpleExoPlayer exoPlayer = activity.getPlayer();

        playerView.setPlayer(exoPlayer);

        String userAgent = Util.getUserAgent(context, "ExoPlayer");
        DefaultDataSourceFactory dataSourceFactory =
                new DefaultDataSourceFactory(context, userAgent);

        Uri videoUri = Uri.parse(uri);
        MediaSource mediaSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

        playerEventListener = new PlayerEventListener();
        exoPlayer.addListener(playerEventListener);
        playerView.setPlaybackPreparer(new PlaybackPreparer() {
            @Override
            public void preparePlayback() {
                Timber.d("preparePlayback");
            }
        });
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare(mediaSource);
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

    public interface PlayerProvider {
        SimpleExoPlayer getPlayer();
    }
}
