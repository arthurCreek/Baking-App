package com.example.android.projectbakingapp.Query;

import com.example.android.projectbakingapp.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/*Recipe Interface for the json file*/
public interface RecipeInterface {
    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipe();
}
