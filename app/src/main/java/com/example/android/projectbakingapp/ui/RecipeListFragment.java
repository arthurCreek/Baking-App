package com.example.android.projectbakingapp.ui;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.projectbakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.Query.RecipeInterface;
import com.example.android.projectbakingapp.Query.RetrofitController;
import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeListFragment extends Fragment {

    private ArrayList<Recipe> recipeArrayList;
    private Unbinder unbinder;
    private LinearLayoutManager linearLayoutManager;
    private RecipeListAdapter recipeListAdapter;
    private OnListFragmentInteractionListener mListener;
    private RecipeActivity recipeActivity;
    SimpleIdlingResource simpleIdlingResource;

    @BindView(R.id.rvRecipeList)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.recipeListEmptyTextView)
    TextView emptyTextView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create view here
        View rootView = inflater.inflate(R.layout.rv_recipe_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        if (isOnline()){
            loadData();
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
        }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void loadData(){
        RecipeInterface recipeInterface = RetrofitController
                .getRetrofit(getContext())
                .create(RecipeInterface.class);

        final retrofit2.Call<ArrayList<Recipe>> recipeCall = recipeInterface.getRecipe();

        recipeCall.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(retrofit2.Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                int status = response.code();

                if (response.isSuccessful()){
                    recipeArrayList = response.body();
                    RecipeActivity.recipeArrayList = recipeArrayList;
                    recipeListAdapter = new RecipeListAdapter(getContext(), recipeArrayList, mListener);
                    recyclerView.setAdapter(recipeListAdapter);
                    recyclerView.setHasFixedSize(true);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ArrayList<Recipe>> call, Throwable t) {

            }
        });
    }

    //Check to see if there is network connectivity to play videos
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
