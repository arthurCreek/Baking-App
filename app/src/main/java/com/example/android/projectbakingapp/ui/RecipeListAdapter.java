package com.example.android.projectbakingapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;

import java.util.ArrayList;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder>{

    public static final String LOG_TAG = RecipeListAdapter.class.getSimpleName();

    private ArrayList<Recipe> recipeArrayList;
    private Context context;
    private final RecipeListFragment.OnListFragmentInteractionListener mListener;

    public RecipeListAdapter(Context context, ArrayList<Recipe> recipeArrayList, RecipeListFragment.OnListFragmentInteractionListener listener) {
        this.recipeArrayList = recipeArrayList;
        this.context = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecipeListAdapter.RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set up the viewholder
        final Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_card_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        //inflate the view and return
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        final RecipeListViewHolder viewHolder = new RecipeListViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeListAdapter.RecipeListViewHolder holder, final int position) {
        holder.nameTextView.setText(recipeArrayList.get(position).getRecipeName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onListFragmentInteraction(recipeArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    public class RecipeListViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;

        public RecipeListViewHolder(View v){
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.recipeNameTextView);
        }
    }
}
