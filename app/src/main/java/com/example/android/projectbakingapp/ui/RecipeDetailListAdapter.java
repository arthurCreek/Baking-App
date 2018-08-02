package com.example.android.projectbakingapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.projectbakingapp.Ingredient;
import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;
import com.example.android.projectbakingapp.Step;

import java.util.ArrayList;

public class RecipeDetailListAdapter extends RecyclerView.Adapter<RecipeDetailListAdapter.RecipeDetailListHolder> {

    private int recipeId;
    private ArrayList<Step> stepList;
    private int recipeServings;
    private ArrayList<String> viewHolderArray;
    private RecipeDetailListFragment.OnDetailListFragmentInteraction mListener;
    private static boolean ADD_BACKSTACK = true;
    private Context context;

    public RecipeDetailListAdapter(Recipe recipe, RecipeDetailListFragment.OnDetailListFragmentInteraction mListener, Context context) {
        this.recipeId = recipe.getRecipeId();
        this.stepList = recipe.getStepList();
        this.recipeServings = recipe.getRecipeServings();
        this.context = context;
        this.viewHolderArray = createViewHolderData();
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecipeDetailListAdapter.RecipeDetailListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set up the viewholder
        context = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_detail_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        //inflate the view and return
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        RecipeDetailListHolder viewHolder = new RecipeDetailListHolder(view);
        return viewHolder;
    }

    //Return array list of strings from stepList
    private ArrayList<String> createViewHolderData() {
        ArrayList<String> stringArrayListData = new ArrayList<>();
        stringArrayListData.add(context.getResources().getString(R.string.recipe_ingredients));
        for (int i = 0; i < stepList.size(); i++){
            stringArrayListData.add(stepList.get(i).getShortDescription());
        }
        return stringArrayListData;
    }

    //Bind views here
    @Override
    public void onBindViewHolder(@NonNull RecipeDetailListAdapter.RecipeDetailListHolder holder, final int position) {
        holder.textView.setText(viewHolderArray.get(position));
        //Create a click listener for itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDetailListFragmentInteraction(recipeId, position, ADD_BACKSTACK);
            }
        });
    }

    @Override
    public int getItemCount() {
        return viewHolderArray.size();
    }

    public class RecipeDetailListHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public RecipeDetailListHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.recipeDetailsTextView);
        }
    }
}
