package com.example.android.projectbakingapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.projectbakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.Query.RecipeInterface;
import com.example.android.projectbakingapp.Query.RetrofitController;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeStepFragment extends android.support.v4.app.Fragment{

    private static final int PREVIOUS_ID = 0;
    private static final int NEXT_ID = 1;

    private static final String STEP_ID_TAG = "stepId";
    private static final String RECIPE_ID_TAG = "recipeId";

    private ArrayList<Recipe> recipeArrayList;
    private Recipe recipe;
    private int stepId;
    private int recipeId;
    private int recipeSize;
    private LinearLayoutManager linearLayoutManager;
    private RecipeIngredientAdapter recipeIngredientAdapter;
    private SimpleExoPlayer player;
    private String urlString;
    private OnStepNavigationClicked mCallback;
    private boolean mTwoPane;
    private Unbinder unbinder;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    SimpleIdlingResource simpleIdlingResource;
    private RecipeActivity recipeActivity;

    @Nullable
    @BindView(R.id.rvIngredientList)
    RecyclerView recyclerView;
    @BindView(R.id.previous_step)
    ImageView previousStep;
    @BindView(R.id.next_step)
    ImageView nextStep;
    @BindView(R.id.step_id_text_view)
    TextView stepIdTextView;
    @Nullable
    @BindView(R.id.no_internet_exo)
    TextView noInternetExo;
    @Nullable
    @BindView(R.id.playerView)
    PlayerView playerView;
    @Nullable
    @BindView(R.id.longDescriptionTextView)
    TextView descriptionTextView;
    @Nullable
    @BindView(R.id.video_thumbnail)
    ImageView videoThumbnail;
    @Nullable
    @BindView(R.id.recipe_image)
    ImageView recipeImage;

    private static final String STARTING_POSITION = "player_position";
    private static final String START_AUTO_PLAY = "start_auto_play";

    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get bundle arguments
        Bundle bundle = this.getArguments();
        stepId = bundle.getInt(STEP_ID_TAG);
        recipeId = bundle.getInt(RECIPE_ID_TAG);
        recipeArrayList = RecipeActivity.recipeArrayList;
        //Check to see if in two pane mode
        if (getActivity().findViewById(R.id.landscape_linear_layout) != null
                || getActivity().findViewById(R.id.portrait_linear_layout) != null){
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        recipeActivity = (RecipeActivity) getActivity();
        //Get idling resource
        simpleIdlingResource = (SimpleIdlingResource) recipeActivity.getIdlingResource();
        if (simpleIdlingResource != null){
            simpleIdlingResource.setIdleState(false);
        }

        //Get the start postion if savedInstanceState is not null for exoplayer
        if (savedInstanceState != null){
            startPosition = savedInstanceState.getLong(STARTING_POSITION);
            startAutoPlay = savedInstanceState.getBoolean(START_AUTO_PLAY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //Create view here
        View rootView = null;

        //At step 0, it is only the ingredients list, populate accordingly
        if (stepId == 0){
            rootView = inflater.inflate(R.layout.rv_ingredient_list, container, false);
            unbinder = ButterKnife.bind(this, rootView);
            recipe = recipeArrayList.get(recipeId-1);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);

            if (!recipe.getImageUrl().equals("")){
                recipeImage.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(recipe.getImageUrl())
                        .into(recipeImage);
            }
            //Previous step image is not necessary on first page
            previousStep.setVisibility(View.INVISIBLE);
            stepIdTextView.setText(getContext().getResources().getString(R.string.ingredients));
            //Set up on click listener
            nextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onStepClicked(stepId, recipeId, NEXT_ID);
                }
            });
            recipeIngredientAdapter = new RecipeIngredientAdapter(recipe, getContext());
            recyclerView.setAdapter(recipeIngredientAdapter);
        } else {
            //else populate accordingle
            rootView = inflater.inflate(R.layout.recipe_step, container, false);
            unbinder = ButterKnife.bind(this, rootView);

            recipe = recipeArrayList.get(recipeId-1);
            recipeSize = recipe.getStepList().size();
            //set on click listeners
            previousStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onStepClicked(stepId, recipeId, PREVIOUS_ID);
                }
            });
            String stepIdString = getContext().getResources().getString(R.string.step) + " " + stepId;
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
            descriptionTextView.setText(recipe.getStepList().get(stepId-1).getLongDescription());
            if (!recipe.getStepList().get(stepId-1).getVideoUrl().equals("")){
                urlString = recipe.getStepList().get(stepId-1).getVideoUrl();
                playerView.setVisibility(View.VISIBLE);
                initializePlayer();
            } else if (!recipe.getStepList().get(stepId-1).getThumbnailUrl().equals("")){
                urlString = recipe.getStepList().get(stepId-1).getThumbnailUrl();
                videoThumbnail.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(urlString)
                        .error(R.drawable.error)
                        .placeholder(R.drawable.android_chef)
                        .into(videoThumbnail);
                initializePlayer();
            } else {
                noInternetExo.setVisibility(View.VISIBLE);
            }
        }

        //set idling state to true
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

    // build the media source with uri
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    //initialize player if not null
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23 && player != null) {
            initializePlayer();
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !mTwoPane){
                hideSystemUi();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                params.width=params.MATCH_PARENT;
                params.height=params.MATCH_PARENT;
                playerView.setLayoutParams(params);
                previousStep.setVisibility(View.GONE);
                nextStep.setVisibility(View.GONE);
                stepIdTextView.setVisibility(View.GONE);
            }

            player.setPlayWhenReady(startAutoPlay);
            player.seekTo(startPosition);
        }
    }

    //initialize player if not null
    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 && player != null)) {
            initializePlayer();
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !mTwoPane){
                hideSystemUi();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                params.width=params.MATCH_PARENT;
                params.height=params.MATCH_PARENT;
                playerView.setLayoutParams(params);
                previousStep.setVisibility(View.GONE);
                nextStep.setVisibility(View.GONE);
                stepIdTextView.setVisibility(View.GONE);
            }
            player.setPlayWhenReady(startAutoPlay);
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

    //save startPosition and release player
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

    //save startPosition and release player
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

    //Method to release player
    private void releasePlayer() {
        if (player != null) {
            startPosition = player.getCurrentPosition();
            startWindow = player.getCurrentWindowIndex();
            startAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


    //Interface for navigation clicks
    public interface OnStepNavigationClicked{
        void onStepClicked(int stepId, int recipeId, int stepDirection);

    }

    //Make sure main activity implements OnStepNavigationClicked
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

    //Set mCallback to null;
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    //Check to see if there is network connectivity to play videos
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    //Save the starting position
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STARTING_POSITION, startPosition);
        if (player != null){
            outState.putBoolean(START_AUTO_PLAY, player.getPlayWhenReady());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
