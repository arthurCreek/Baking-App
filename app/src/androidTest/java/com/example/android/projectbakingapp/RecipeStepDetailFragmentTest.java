package com.example.android.projectbakingapp;


import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.projectbakingapp.ui.RecipeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RecipeStepDetailFragmentTest {

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<RecipeActivity> recipeActivityActivityTestRule =
            new ActivityTestRule<>(RecipeActivity.class);

    @Before
    public void registerIdlingResource(){
        idlingResource = recipeActivityActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        recipeActivityActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipeActivityActivityTestRule.getActivity()
                        .onListFragmentInteraction(
                                recipeActivityActivityTestRule.getActivity().recipeArrayList.get(0)
                        );

            }
        });
    }

    @Test
    public void testIngredientList(){
        recipeActivityActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipeActivityActivityTestRule.getActivity()
                        .onDetailListFragmentInteraction(1, 0, false);
            }
        });

        onView(withId(R.id.rvIngredientList))
                .perform(RecyclerViewActions
                .scrollToPosition(0))
                .check(matches(isDisplayed()));

        onView(withId(R.id.rvIngredientList))
                .check(matches(hasDescendant(withId(R.id.ingredientTextView))));
    }

    @Test
    public void textForwardButton(){
        recipeActivityActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipeActivityActivityTestRule.getActivity()
                        .onDetailListFragmentInteraction(1, 0, false);
            }
        });

        onView(withId(R.id.next_step))
                .perform(ViewActions.click());
    }

    @Test
    public void textBackButton(){
        recipeActivityActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipeActivityActivityTestRule.getActivity()
                        .onDetailListFragmentInteraction(1, 3, false);
            }
        });

        onView(withId(R.id.previous_step))
                .perform(ViewActions.click());
    }

    @Test
    public void testStep() throws InterruptedException {
        recipeActivityActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipeActivityActivityTestRule.getActivity()
                        .onDetailListFragmentInteraction(1, 1, false);
            }
        });

        Thread.sleep(2000);

        onView(withId(R.id.playerView))
                .check(matches(isDisplayed()));

        onView(withId(R.id.longDescriptionTextView))
                .check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource(){
        if (idlingResource != null){
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
