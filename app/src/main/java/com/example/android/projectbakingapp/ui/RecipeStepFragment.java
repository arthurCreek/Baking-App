package com.example.android.projectbakingapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class RecipeStepFragment extends android.support.v4.app.Fragment{

    public static final String LOG_TAG = RecipeStepFragment.class.getSimpleName();

    private static final int PREVIOUS_ID = 0;
    private static final int NEXT_ID = 1;

    //TODO move these to strings.xml
    private static final String STEP_TEXT = "Step ";
    private static final String INGREDIENTS = "Ingredients";

    private ArrayList<Recipe> recipeArrayList;
    private Recipe recipe;
    private int stepId;
    private int recipeId;
    private int recipeSize;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecipeIngredientAdapter recipeIngredientAdapter;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private String urlString;
    private OnStepNavigationClicked mCallback;
    private ImageView previousStep;
    private ImageView nextStep;
    private TextView stepIdTextView;
    private TextView noInternetExo;
    private boolean mTwoPane;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    SimpleIdlingResource simpleIdlingResource;
    private RecipeActivity recipeActivity;

    private static final String STARTING_POSITION = "player_position";

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
        recipeSize = recipe.getStepList().size();
        if (getActivity().findViewById(R.id.landscape_linear_layout) != null){
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        recipeActivity = (RecipeActivity) getActivity();
        simpleIdlingResource = (SimpleIdlingResource) recipeActivity.getIdlingResource();
        if (simpleIdlingResource != null){
            simpleIdlingResource.setIdleState(false);
        }

        if (savedInstanceState != null){
            startPosition = savedInstanceState.getLong(STARTING_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;


        if (stepId == 0){
            rootView = inflater.inflate(R.layout.rv_ingredient_list, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.rvIngredientList);

            previousStep = (ImageView) rootView.findViewById(R.id.previous_step);
            nextStep = (ImageView) rootView.findViewById(R.id.next_step);
            stepIdTextView = (TextView) rootView.findViewById(R.id.step_id_text_view);

            previousStep.setVisibility(View.INVISIBLE);
            stepIdTextView.setText(INGREDIENTS);
            nextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onStepClicked(stepId, recipeId, NEXT_ID);
                }
            });

            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            recipeIngredientAdapter = new RecipeIngredientAdapter(recipe);
            recyclerView.setAdapter(recipeIngredientAdapter);
        } else {
            rootView = inflater.inflate(R.layout.recipe_step, container, false);
            previousStep = (ImageView) rootView.findViewById(R.id.previous_step);
            nextStep = (ImageView) rootView.findViewById(R.id.next_step);
            stepIdTextView = (TextView) rootView.findViewById(R.id.step_id_text_view);

            previousStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onStepClicked(stepId, recipeId, PREVIOUS_ID);
                }
            });
            String stepIdString = STEP_TEXT + stepId;
            stepIdTextView.setText(stepIdString);
            if (stepId < recipeSize){
                nextStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onStepClicked(stepId, recipeId, NEXT_ID);
                    }
                });
            } else {
                nextStep.setVisibility(View.INVISIBLE);
            }

            playerView = (PlayerView) rootView.findViewById(R.id.playerView);
            TextView descriptionTextView = (TextView) rootView.findViewById(R.id.longDescriptionTextView);
            descriptionTextView.setText(recipe.getStepList().get(stepId-1).getLongDescription());
            if (isOnline()){
                if (!recipe.getStepList().get(stepId-1).getVideoUrl().equals("")){
                    urlString = recipe.getStepList().get(stepId-1).getVideoUrl();
                    playerView.setVisibility(View.VISIBLE);
                    initializePlayer();
                } else if (!recipe.getStepList().get(stepId-1).getThumbnailUrl().equals("")){
                    urlString = recipe.getStepList().get(stepId-1).getThumbnailUrl();
                    playerView.setVisibility(View.VISIBLE);
                    initializePlayer();
                }
            } else if (!recipe.getStepList().get(stepId-1).getVideoUrl().equals("")
                    || !recipe.getStepList().get(stepId-1).getThumbnailUrl().equals("")){
                noInternetExo = (TextView) rootView.findViewById(R.id.no_internet_exo);
                noInternetExo.setVisibility(View.VISIBLE);
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
        if (Util.SDK_INT > 23 && player != null) {
            initializePlayer();
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                if (!mTwoPane){
                    hideSystemUi();
                }
                //First Hide other objects (listview or recyclerview), better hide them using Gone.
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                params.width=params.MATCH_PARENT;
                params.height=params.MATCH_PARENT;
                playerView.setLayoutParams(params);
                previousStep.setVisibility(View.GONE);
                nextStep.setVisibility(View.GONE);
                stepIdTextView.setVisibility(View.GONE);
            }
            player.setPlayWhenReady(true);
            player.seekTo(startPosition);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 && player != null)) {
            initializePlayer();
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                if (!mTwoPane){
                    hideSystemUi();
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                params.width=params.MATCH_PARENT;
                params.height=params.MATCH_PARENT;
                playerView.setLayoutParams(params);
                previousStep.setVisibility(View.GONE);
                nextStep.setVisibility(View.GONE);
                stepIdTextView.setVisibility(View.GONE);
            }
            player.setPlayWhenReady(true);
            player.seekTo(startPosition);
        }
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
            startPosition = player.getCurrentPosition();
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
                startPosition = player.getCurrentPosition();
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


    public interface OnStepNavigationClicked{
        public void onStepClicked(int stepId, int recipeId, int stepDirection);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepNavigationClicked) {
            mCallback = (OnStepNavigationClicked) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailListFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STARTING_POSITION, startPosition);
    }
}
