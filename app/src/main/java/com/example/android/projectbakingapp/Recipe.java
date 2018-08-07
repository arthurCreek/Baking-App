package com.example.android.projectbakingapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Recipe {

    @SerializedName("id")
    private int recipeId;
    @SerializedName("name")
    private String recipeName;
    @SerializedName("ingredients")
    private ArrayList<Ingredient> ingredientsList;
    @SerializedName("steps")
    private ArrayList<Step> stepList;
    @SerializedName("servings")
    private int recipeServings;
    @SerializedName("image")
    private String imageUrl;

    public Recipe(int recipeId, String recipeName, ArrayList<Ingredient> ingredientsList, ArrayList<Step> stepList, int recipeServings, String imageUrl) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientsList = ingredientsList;
        this.stepList = stepList;
        this.recipeServings = recipeServings;
        this.imageUrl = imageUrl;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public ArrayList<Ingredient> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(ArrayList<Ingredient> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public ArrayList<Step> getStepList() {
        return stepList;
    }

    public void setStepList(ArrayList<Step> stepList) {
        this.stepList = stepList;
    }

    public int getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(int recipeServings) {
        this.recipeServings = recipeServings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
