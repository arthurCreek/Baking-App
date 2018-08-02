package com.example.android.projectbakingapp.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeListFragment extends Fragment {

    private ArrayList<Recipe> recipeArrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecipeListAdapter recipeListAdapter;
    private OnListFragmentInteractionListener mListener;
    private RecipeActivity recipeActivity;

    SimpleIdlingResource simpleIdlingResource;

    public RecipeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeActivity = (RecipeActivity) getActivity();

        //Get the idling resource and save to simpleIdlingResource
        simpleIdlingResource = (SimpleIdlingResource) recipeActivity.getIdlingResource();
        if (simpleIdlingResource != null){
            simpleIdlingResource.setIdleState(false);
        }
        recipeArrayList = QueryUtils.extractRecipes(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create view here
        View rootView = inflater.inflate(R.layout.rv_recipe_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvRecipeList);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        recipeListAdapter = new RecipeListAdapter(getContext(), recipeArrayList, mListener);
        recyclerView.setAdapter(recipeListAdapter);

        simpleIdlingResource.setIdleState(true);

        return rootView;
    }

    //Make sure main activity implements OnListFragmentInteractionListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    // Make mListener null
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Recipe recipe);
    }
}
