package com.example.android.projectbakingapp.ui;

import android.content.Intent;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.projectbakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;
import com.example.android.projectbakingapp.RecipeWidgetProvider;
import com.example.android.projectbakingapp.ui.RecipeListFragment.OnListFragmentInteractionListener;

public class RecipeActivity extends AppCompatActivity
        implements OnListFragmentInteractionListener,
        RecipeDetailListFragment.OnDetailListFragmentInteraction, RecipeStepFragment.OnStepNavigationClicked{

    private static final int INGREDIENT_INDEX = 0;
    private static final int PREVIOUS_ID = 0;
    private static final int NEXT_ID = 1;
    private static final boolean DONT_ADD_BACKSTACK = false;
    private static final boolean ADD_BACKSTACK = true;
    private static final String RECIPE_LIST_TAG = "Recipe_List";
    private static final String RECIPE_DETAIL_LIST_TAG = "Recipe_Detail_List";
    private static final String RECIPE_STEP_TAG = "Recipe_Step";
    private static final String STEP_ID_BUNDLE = "stepId";
    private static final String RECIPE_ID_BUNDLE = "recipeId";
    SimpleIdlingResource simpleIdlingResource;
    private boolean mTwoPane;

    protected boolean wasLaunchedFromRecents() {
        return (getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;
    }


    public IdlingResource getIdlingResource(){
        if (simpleIdlingResource == null){
            simpleIdlingResource = new SimpleIdlingResource();
        }
        return simpleIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Bundle extras = getIntent().getExtras();
        getIdlingResource();

        if (findViewById(R.id.landscape_linear_layout) != null || findViewById(R.id.portrait_linear_layout) != null){
            mTwoPane = true;

            if (savedInstanceState == null){
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RecipeListFragment recipeListFragment = new RecipeListFragment();
                fragmentTransaction.add(R.id.detail_and_step_list, recipeListFragment, RECIPE_LIST_TAG);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
                if (extras != null && !wasLaunchedFromRecents()){
                    int widgetIndexPosition = extras.getInt(RecipeWidgetProvider.EXTRA_ITEM);
                    onListFragmentInteraction(QueryUtils.extractRecipes(this).get(widgetIndexPosition));
                    onDetailListFragmentInteraction(widgetIndexPosition+1, INGREDIENT_INDEX, ADD_BACKSTACK);
                }
            }
        } else {
            mTwoPane = false;
        }

        if (savedInstanceState == null && !mTwoPane){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            RecipeListFragment recipeListFragment = new RecipeListFragment();
            fragmentTransaction.add(R.id.displayList, recipeListFragment, RECIPE_LIST_TAG);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
            if (extras != null && !wasLaunchedFromRecents()){
                int widgetIndexPosition = extras.getInt(RecipeWidgetProvider.EXTRA_ITEM);
                onListFragmentInteraction(QueryUtils.extractRecipes(this).get(widgetIndexPosition));
                onDetailListFragmentInteraction(widgetIndexPosition+1, INGREDIENT_INDEX, ADD_BACKSTACK);
            }
        }
    }

    @Override
    public void onListFragmentInteraction(Recipe recipe) {
        Toast.makeText(this, recipe.getRecipeName(), Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        RecipeDetailListFragment recipeDetailListFragment = new RecipeDetailListFragment();
        if (mTwoPane){
            fragmentTransaction2.replace(R.id.detail_and_step_list, recipeDetailListFragment, RECIPE_DETAIL_LIST_TAG);
        } else {
            fragmentTransaction2.replace(R.id.displayList, recipeDetailListFragment, RECIPE_DETAIL_LIST_TAG);
        }
        fragmentTransaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction2.addToBackStack(null);
        Bundle bundle = new Bundle();
        bundle.putInt("id", recipe.getRecipeId());
        recipeDetailListFragment.setArguments(bundle);
        fragmentTransaction2.commit();

    }

    @Override
    public void onDetailListFragmentInteraction(int recipeId, int stepPosition, boolean addBackStack) {
        Toast.makeText(this, String.valueOf(stepPosition), Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
        }
        if (mTwoPane){
            fragmentTransaction3.replace(R.id.media_description_tablet, recipeStepFragment, RECIPE_STEP_TAG);
        } else {
            fragmentTransaction3.replace(R.id.displayList, recipeStepFragment, RECIPE_STEP_TAG);
            fragmentTransaction3.addToBackStack(null);
        }
        fragmentTransaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        Bundle stepBundle = new Bundle();
        stepBundle.putInt(STEP_ID_BUNDLE, stepPosition);
        stepBundle.putInt(RECIPE_ID_BUNDLE, recipeId);
        recipeStepFragment.setArguments(stepBundle);
        fragmentTransaction3.commit();
    }

    @Override
    public void onStepClicked(int stepId, int recipeId, int stepDirection) {
        if (stepDirection == PREVIOUS_ID){
            onDetailListFragmentInteraction(recipeId, stepId-1, DONT_ADD_BACKSTACK);
        } else if (stepDirection == NEXT_ID){
            onDetailListFragmentInteraction(recipeId, stepId+1, DONT_ADD_BACKSTACK);
        }
    }

    @Override
    public void onBackPressed() {
        if (mTwoPane && getSupportFragmentManager().findFragmentByTag(RECIPE_STEP_TAG) != null){
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.media_description_tablet)).commit();
        }
        else super.onBackPressed();
    }

}
