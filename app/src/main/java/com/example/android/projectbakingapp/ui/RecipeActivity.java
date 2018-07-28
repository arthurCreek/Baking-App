package com.example.android.projectbakingapp.ui;

import android.content.Intent;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.projectbakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;
import com.example.android.projectbakingapp.RecipeWidgetProvider;
import com.example.android.projectbakingapp.ui.RecipeListFragment.OnListFragmentInteractionListener;

public class RecipeActivity extends AppCompatActivity implements OnListFragmentInteractionListener, RecipeDetailListFragment.OnDetailListFragmentInteraction{

    private static int INGREDIENT_INDEX = 0;
    SimpleIdlingResource simpleIdlingResource;

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

        if (savedInstanceState == null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            RecipeListFragment recipeListFragment = new RecipeListFragment();
            fragmentTransaction.add(R.id.displayList, recipeListFragment, "Recipe__List");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
            if (extras != null && !wasLaunchedFromRecents()){
                int widgetIndexPosition = extras.getInt(RecipeWidgetProvider.EXTRA_ITEM);
                onListFragmentInteraction(QueryUtils.extractRecipes(this).get(widgetIndexPosition));
                onDetailListFragmentInteraction(widgetIndexPosition+1, INGREDIENT_INDEX);
            }
        }
    }

    @Override
    public void onListFragmentInteraction(Recipe recipe) {
        Toast.makeText(this, recipe.getRecipeName(), Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        RecipeDetailListFragment recipeDetailListFragment = new RecipeDetailListFragment();
        fragmentTransaction2.replace(R.id.displayList, recipeDetailListFragment, "Recipe_Detail_List");
        fragmentTransaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction2.addToBackStack(null);
        Bundle bundle = new Bundle();
        bundle.putInt("id", recipe.getRecipeId());
        recipeDetailListFragment.setArguments(bundle);
        fragmentTransaction2.commit();

    }

    @Override
    public void onDetailListFragmentInteraction(int recipeId, int stepPosition) {
        Toast.makeText(this, String.valueOf(stepPosition), Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        fragmentTransaction3.replace(R.id.displayList, recipeStepFragment, "Recipe_Step");
        fragmentTransaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction3.addToBackStack(null);
        Bundle stepBundle = new Bundle();
        stepBundle.putInt("stepId", stepPosition);
        stepBundle.putInt("recipeId", recipeId);
        recipeStepFragment.setArguments(stepBundle);
        fragmentTransaction3.commit();
    }
}
