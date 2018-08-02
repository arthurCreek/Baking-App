package com.example.android.projectbakingapp.Query;

import android.content.Context;

import com.example.android.projectbakingapp.Ingredient;
import com.example.android.projectbakingapp.Recipe;
import com.example.android.projectbakingapp.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class QueryUtils {

    //Query the JSON from assets here and parse through it
    private QueryUtils(){
    }

    public static ArrayList<Recipe> extractRecipes(Context context){

        ArrayList<Recipe> recipeArrayList = new ArrayList<>();

        String jsonResponse = "";

        jsonResponse = loadJSONFromAsset(context);

        try {
            JSONArray root = new JSONArray(jsonResponse);
            for (int i = 0; i < root.length(); i++){
                ArrayList<Ingredient> ingredientsResponse = new ArrayList<>();
                ArrayList<Step> stepResponse = new ArrayList<>();
                JSONObject recipeObject = root.getJSONObject(i);
                String recipeIdString = recipeObject.getString("id");
                int recipeId = Integer.parseInt(recipeIdString);
                String recipeName = recipeObject.getString("name");
                JSONArray ingredientArray = recipeObject.getJSONArray("ingredients");
                for (int j = 0; j < ingredientArray.length(); j++){
                    JSONObject ingredientItem = ingredientArray.getJSONObject(j);
                    double quantity = ingredientItem.getDouble("quantity");
                    String measure = ingredientItem.getString("measure");
                    String ingredientString = ingredientItem.getString("ingredient");
                    ingredientString = ingredientString.substring(0,1).toUpperCase()
                            + ingredientString.substring(1);
                    Ingredient ingredient = new Ingredient(quantity, measure, ingredientString);
                    ingredientsResponse.add(ingredient);
                }

                JSONArray stepsArray = recipeObject.getJSONArray("steps");
                for (int k = 0; k < stepsArray.length(); k++){
                    JSONObject stepItem = stepsArray.getJSONObject(k);
                    int stepId = stepItem.getInt("id");
                    String shortDescription = stepItem.getString("shortDescription");
                    String longDescription = stepItem.getString("description");
                    String videoUrl = stepItem.optString("videoURL");
                    String thumbnailUrl = stepItem.optString("thumbnailURL");
                    Step step = new Step(stepId, shortDescription, longDescription, videoUrl, thumbnailUrl);
                    stepResponse.add(step);
                }

                int recipeServings = recipeObject.getInt("servings");

                Recipe recipe = new Recipe(recipeId, recipeName, ingredientsResponse, stepResponse, recipeServings);
                recipeArrayList.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipeArrayList;

    }

    //Load the json asset
    private static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("baking.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
