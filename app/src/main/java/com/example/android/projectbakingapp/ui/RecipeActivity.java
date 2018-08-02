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

    //Index at which it is only the ingredient list
    private static final int INGREDIENT_INDEX = 0;
    //Static ids for fragment to know if forward or back button was pressed
    private static final int PREVIOUS_ID = 0;
    private static final int NEXT_ID = 1;
    //Static booleans to help with adding to back stack on tablet layouts
    private static final boolean DONT_ADD_BACKSTACK = false;
    private static final boolean ADD_BACKSTACK = true;
    //Static tags for developer to see to help with fragments
    private static final String RECIPE_LIST_TAG = "Recipe_List";
    private static final String RECIPE_DETAIL_LIST_TAG = "Recipe_Detail_List";
    private static final String RECIPE_STEP_TAG = "Recipe_Step";
    private static final String STEP_ID_BUNDLE = "stepId";
    private static final String RECIPE_ID_BUNDLE = "recipeId";

    SimpleIdlingResource simpleIdlingResource;
    //Boolean to determine if launched in twoPane
    private boolean mTwoPane;

    //Check to see if app was launched from recents
    protected boolean wasLaunchedFromRecents() {
        return (getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;
    }

    //Get idling resource
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

        //Get extras if there are any
        Bundle extras = getIntent().getExtras();
        getIdlingResource();

        //Check if app is in twoPane mode and if it is in portrait or landscape mode
        if (findViewById(R.id.landscape_linear_layout) != null || findViewById(R.id.portrait_linear_layout) != null){
            mTwoPane = true;

            //Create fragments if savedInstance is null
            if (savedInstanceState == null){
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RecipeListFragment recipeListFragment = new RecipeListFragment();
                fragmentTransaction.add(R.id.detail_and_step_list, recipeListFragment, RECIPE_LIST_TAG);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
                //If app was launched from widget, open up the correct ingredients list
                if (extras != null && !wasLaunchedFromRecents()){
                    int widgetIndexPosition = extras.getInt(RecipeWidgetProvider.EXTRA_ITEM);
                    onListFragmentInteraction(QueryUtils.extractRecipes(this).get(widgetIndexPosition));
                    onDetailListFragmentInteraction(widgetIndexPosition+1, INGREDIENT_INDEX, ADD_BACKSTACK);
                }
            }
        } else {
            mTwoPane = false;
        }

        //If in single pane mode, create fragment
        if (savedInstanceState == null && !mTwoPane){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            RecipeListFragment recipeListFragment = new RecipeListFragment();
            fragmentTransaction.add(R.id.displayList, recipeListFragment, RECIPE_LIST_TAG);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
            //If app was lanched from widget and in single pane mode, open correct ingredients list
            if (extras != null && !wasLaunchedFromRecents()){
                int widgetIndexPosition = extras.getInt(RecipeWidgetProvider.EXTRA_ITEM);
                onListFragmentInteraction(QueryUtils.extractRecipes(this).get(widgetIndexPosition));
                onDetailListFragmentInteraction(widgetIndexPosition+1, INGREDIENT_INDEX, ADD_BACKSTACK);
            }
        }
    }

    //Interface to open list fragment
    @Override
    public void onListFragmentInteraction(Recipe recipe) {
        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        RecipeDetailListFragment recipeDetailListFragment = new RecipeDetailListFragment();
        //Determine if in two pane mode and replace containers accordingly
        if (mTwoPane){
            fragmentTransaction2.replace(R.id.detail_and_step_list, recipeDetailListFragment, RECIPE_DETAIL_LIST_TAG);
        } else {
            fragmentTransaction2.replace(R.id.displayList, recipeDetailListFragment, RECIPE_DETAIL_LIST_TAG);
        }
        fragmentTransaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction2.addToBackStack(null);
        //Add the recipe ID in bundle
        Bundle bundle = new Bundle();
        bundle.putInt("id", recipe.getRecipeId());
        recipeDetailListFragment.setArguments(bundle);
        fragmentTransaction2.commit();

    }

    //Interface to create detail list fragment
    @Override
    public void onDetailListFragmentInteraction(int recipeId, int stepPosition, boolean addBackStack) {
        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        //Back stack will be popped to remove step additional step fragment
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
        //Put step position and recipe id inn bundle
        Bundle stepBundle = new Bundle();
        stepBundle.putInt(STEP_ID_BUNDLE, stepPosition);
        stepBundle.putInt(RECIPE_ID_BUNDLE, recipeId);
        recipeStepFragment.setArguments(stepBundle);
        fragmentTransaction3.commit();
    }

    //Based on previous or forward click, open correct step
    @Override
    public void onStepClicked(int stepId, int recipeId, int stepDirection) {
        if (stepDirection == PREVIOUS_ID){
            onDetailListFragmentInteraction(recipeId, stepId-1, DONT_ADD_BACKSTACK);
        } else if (stepDirection == NEXT_ID){
            onDetailListFragmentInteraction(recipeId, stepId+1, DONT_ADD_BACKSTACK);
        }
    }

    //Helps with two pane mode, if back is pressed while step fragment is active remove fragment
    @Override
    public void onBackPressed() {
        if (mTwoPane && getSupportFragmentManager().findFragmentByTag(RECIPE_STEP_TAG) != null){
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.media_description_tablet)).commit();
        }
        else super.onBackPressed();
    }

}
