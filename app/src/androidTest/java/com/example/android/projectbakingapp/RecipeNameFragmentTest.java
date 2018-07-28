package com.example.android.projectbakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.example.android.projectbakingapp.ui.RecipeActivity;
import com.example.android.projectbakingapp.ui.RecipeListFragment;

import static android.support.test.espresso.assertion.ViewAssertions.matches;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeNameFragmentTest {

    private final static String RECIPE_NAME = "Nutella Pie";

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<RecipeActivity> recipeActivityActivityTestRule =
            new ActivityTestRule<>(RecipeActivity.class);

    @Before
    public void registerIdlingResources(){
        idlingResource = recipeActivityActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        recipeActivityActivityTestRule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.displayList, new RecipeListFragment(), "Recipe_List")
                .addToBackStack(null)
                .commit();
    }
    @Test
    public void testRecipeNameFragment(){
        onView(withId(R.id.rvRecipeList))
                .perform(RecyclerViewActions
                        .scrollToPosition(0));

        onView(withText(RECIPE_NAME))
                .check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource(){
        if (idlingResource != null){
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
