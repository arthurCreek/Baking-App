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

import java.util.ArrayList;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.RecipeIngredientHolder> {

    private ArrayList<Ingredient> ingredientArrayList;
    Context context;

    public RecipeIngredientAdapter(Recipe recipe, Context context) {
        this.ingredientArrayList = recipe.getIngredientsList();
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeIngredientAdapter.RecipeIngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // set up the viewholder
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_ingredient_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        //inflate the view and return
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        RecipeIngredientHolder viewHolder= new RecipeIngredientHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientAdapter.RecipeIngredientHolder holder, int position) {
        //Bind views here
        Ingredient ingredient = ingredientArrayList.get(position);
        String ingredientString;
        StringBuilder ingredientBuilder = new StringBuilder();
        ingredientBuilder.append(context.getResources().getString(R.string.ingredients) + ":\n");
        ingredientBuilder.append(ingredient.getIngredients());
        ingredientString = ingredientBuilder.toString();
        holder.ingredientTesxtView.setText(ingredientString);

        String measureString;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getResources().getString(R.string.quantity) + ":\n");
        stringBuilder.append(ingredient.getIngredientQuantity());
        stringBuilder.append(" ");
        stringBuilder.append(ingredient.getIngredientMeasure());
        measureString = stringBuilder.toString();
        holder.measureTextView.setText(measureString);

    }

    @Override
    public int getItemCount() {
        return ingredientArrayList.size();
    }

    public class RecipeIngredientHolder extends RecyclerView.ViewHolder{
        TextView ingredientTesxtView;
        TextView measureTextView;

        public RecipeIngredientHolder(View itemView) {
            super(itemView);

            ingredientTesxtView = (TextView) itemView.findViewById(R.id.ingredientTextView);
            measureTextView = (TextView) itemView.findViewById(R.id.quantityTextView);
        }
    }
}
