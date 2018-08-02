package com.example.android.projectbakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeDetailListFragment extends Fragment {

    private Recipe recipe;
    private Unbinder unbinder;
    private LinearLayoutManager linearLayoutManager;
    private RecipeDetailListAdapter recipeDetailListAdapter;
    private OnDetailListFragmentInteraction mListener;
    private static final String ID_BUNDLE = "id";

    @BindView(R.id.rvRecipeDetailList)
    RecyclerView recyclerView;

    public RecipeDetailListFragment() {
        //Public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initiate array list of recipes and get bundle argumets
        ArrayList<Recipe> recipes = QueryUtils.extractRecipes(getContext());
        Bundle bundle = this.getArguments();
        int recipeIdBundleArg = bundle.getInt(ID_BUNDLE);
        recipe = recipes.get(recipeIdBundleArg-1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Create view here
        View view = inflater.inflate(R.layout.rv_detail_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecor);

        recipeDetailListAdapter = new RecipeDetailListAdapter(recipe, mListener, getContext());
        recyclerView.setAdapter(recipeDetailListAdapter);


        return view;
    }

    //Make sure main activity implements the OnDetailListFragmentInteraction
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailListFragmentInteraction) {
            mListener = (OnDetailListFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailListFragmentInteraction");
        }
    }

    //Clear mListener
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Interface for interaction
    public interface OnDetailListFragmentInteraction {
        void onDetailListFragmentInteraction(int recipeId, int stepPosition, boolean addBackstack);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
