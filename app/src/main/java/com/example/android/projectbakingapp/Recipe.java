package com.example.android.projectbakingapp;

import java.util.ArrayList;

public class Recipe {

    private int recipeId;
    private String recipeName;
    private ArrayList<Ingredient> ingredientsList;
    private ArrayList<Step> stepList;
    private int recipeServings;

    public Recipe(int recipeId, String recipeName, ArrayList<Ingredient> ingredientsList, ArrayList<Step> stepList, int recipeServings) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientsList = ingredientsList;
        this.stepList = stepList;
        this.recipeServings = recipeServings;
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
}
