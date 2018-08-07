package com.example.android.projectbakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.projectbakingapp.Ingredient;
import com.example.android.projectbakingapp.Query.QueryUtils;
import com.example.android.projectbakingapp.Query.RecipeInterface;
import com.example.android.projectbakingapp.Query.RetrofitController;
import com.example.android.projectbakingapp.R;
import com.example.android.projectbakingapp.Recipe;
import com.example.android.projectbakingapp.RecipeWidgetProvider;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

public class RecipeWidgetsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Ingredient> ingredientArrayList;
    private ArrayList<String> ingredientStringList;
    private int appWidgetId;

    public RecipeWidgetsRemoteViewsFactory(Context mContext, Intent intent) {
        this.mContext = mContext;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        }



    @Override
    public void onDataSetChanged() {
        loadData();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (recipeArrayList == null){
            return 0;
        } else {
            return recipeArrayList.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {

        //Get remote views at certain positions
        Recipe recipe =  recipeArrayList.get(position);
        String recipeName = recipe.getRecipeName();

        String ingredientText = ingredientStringList.get(position);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.stackview_item);
        remoteViews.setTextViewText(R.id.sv_recipe_name, recipeName
                + " " + mContext.getResources().getString(R.string.preview));
        remoteViews.setTextViewText(R.id.sv_ingredient_list, ingredientText);

        Bundle extras = new Bundle();
        extras.putInt(RecipeWidgetProvider.EXTRA_ITEM, position);

        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);
        fillIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        remoteViews.setOnClickFillInIntent(R.id.sv_linear_container, fillIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //Check to see if there is network connectivity to play videos
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void loadData(){
        RecipeInterface recipeInterface = RetrofitController
                .getRetrofit(mContext)
                .create(RecipeInterface.class);

        final retrofit2.Call<ArrayList<Recipe>> recipeCall = recipeInterface.getRecipe();

        recipeCall.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(retrofit2.Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                int status = response.code();

                if (response.isSuccessful()){
                    recipeArrayList = response.body();
                    ingredientStringList = new ArrayList<>();
                    for (int i = 0; i < recipeArrayList.size(); i++) {
                        ingredientArrayList = recipeArrayList.get(i).getIngredientsList();
                        StringBuilder builder = new StringBuilder();
                        for (int j = 0; j < ingredientArrayList.size(); j++) {
                            builder.append(ingredientArrayList.get(j).getIngredients());
                            builder.append(" ");
                            builder.append(mContext.getResources().getString(R.string.quantity));
                            builder.append(": ");
                            builder.append(ingredientArrayList.get(j).getIngredientQuantity());
                            builder.append(" ");
                            builder.append(ingredientArrayList.get(j).getIngredientMeasure());
                            if (j != ingredientArrayList.size() - 1) {
                                builder.append("\n");
                            }
                        }
                        String ingredientString = builder.toString();
                        ingredientStringList.add(ingredientString);
                    }
                    setRecipeStringList(ingredientStringList);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ArrayList<Recipe>> call, Throwable t) {

            }
        });
    }

    public void setRecipeStringList(ArrayList<String> recipeStringListResposnse){
        ingredientStringList = recipeStringListResposnse;

    }
}
