package com.example.android.projectbakingapp.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.projectbakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class RecipeStepFragment extends android.support.v4.app.Fragment {

    private ArrayList<Recipe> recipeArrayList;
    private Recipe recipe;
    private int stepId;
    private int recipeId;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecipeIngredientAdapter recipeIngredientAdapter;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private String urlString;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    SimpleIdlingResource simpleIdlingResource;
    private RecipeActivity recipeActivity;

    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeArrayList = QueryUtils.extractRecipes(getContext());
        Bundle bundle = this.getArguments();
        stepId = bundle.getInt("stepId");
        recipeId = bundle.getInt("recipeId");
        recipe = recipeArrayList.get(recipeId-1);

        recipeActivity = (RecipeActivity) getActivity();
        simpleIdlingResource = (SimpleIdlingResource) recipeActivity.getIdlingResource();
        if (simpleIdlingResource != null){
            simpleIdlingResource.setIdleState(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView;

        if (stepId == 0){
            rootView = inflater.inflate(R.layout.rv_ingredient_list, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.rvIngredientList);

            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            recipeIngredientAdapter = new RecipeIngredientAdapter(recipe);
            recyclerView.setAdapter(recipeIngredientAdapter);
        } else {
            rootView = inflater.inflate(R.layout.recipe_step, container, false);
            playerView = (PlayerView) rootView.findViewById(R.id.playerView);
            TextView descriptionTextView = (TextView) rootView.findViewById(R.id.longDescriptionTextView);
            TextView videoUrlTextView = (TextView) rootView.findViewById(R.id.videoUrlTextView);
            descriptionTextView.setText(recipe.getStepList().get(stepId-1).getLongDescription());
            videoUrlTextView.setText(recipe.getStepList().get(stepId-1).getVideoUrl());
            if (!recipe.getStepList().get(stepId-1).getVideoUrl().equals("")){
                urlString = recipe.getStepList().get(stepId-1).getVideoUrl();
                playerView.setVisibility(View.VISIBLE);
                initializePlayer();
            } else if (!recipe.getStepList().get(stepId-1).getThumbnailUrl().equals("")){
                urlString = recipe.getStepList().get(stepId-1).getThumbnailUrl();
                playerView.setVisibility(View.VISIBLE);
                initializePlayer();
            }
        }

        simpleIdlingResource.setIdleState(true);

        return rootView;
    }

    private void initializePlayer() {
        if (player == null) {
            // Create an instance of the ExoPlayer.
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(startAutoPlay);
            player.seekTo(startWindow, startPosition);

            Uri uri = Uri.parse(urlString);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (Util.SDK_INT > 23 && player != null) {
//            initializePlayer();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        hideSystemUi();
//        if ((Util.SDK_INT <= 23 && player != null)) {
//            initializePlayer();
//        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null){
            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null){
            if (Util.SDK_INT > 23) {
                releasePlayer();
            }
        }
    }

    private void releasePlayer() {
        if (player != null) {
            startPosition = player.getCurrentPosition();
            startWindow = player.getCurrentWindowIndex();
            startAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
}
