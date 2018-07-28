package com.example.android.projectbakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.ui.RecipeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeStepFragmentTest {

    private final static String RECIPE_INGREDIENTS = "Recipe Ingredients";
    private final static String FINISHING_STEPS = "Finishing Steps";

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<RecipeActivity> recipeActivityActivityTestRule =
            new ActivityTestRule<>(RecipeActivity.class);

    @Before
    public void registerIdlingResources(){
        idlingResource = recipeActivityActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        recipeActivityActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipeActivityActivityTestRule.getActivity()
                        .onListFragmentInteraction(
                                (QueryUtils.extractRecipes(recipeActivityActivityTestRule.getActivity().getApplicationContext())).get(0));
            }
        });
    }

    @Test
    public void testRecipeNameFragment(){

        onView(withId(R.id.rvRecipeDetailList))
                .perform(RecyclerViewActions
                        .scrollToPosition(0));

        onView(withText(RECIPE_INGREDIENTS))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLastStep(){
        onView(withId(R.id.rvRecipeDetailList))
                .perform(RecyclerViewActions
                .scrollToPosition(7));

        onView(withText(FINISHING_STEPS))
                .check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource(){
        if (idlingResource != null){
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
