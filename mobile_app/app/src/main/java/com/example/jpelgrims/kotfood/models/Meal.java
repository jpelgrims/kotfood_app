package com.example.jpelgrims.kotfood.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Meal {

    private String name = "";
    private String recipe = "";
    private String imageName = "";
    private List<Ingredient> ingredients = new ArrayList<>();

    public Meal(String name, String recipe, String imageName, List<Ingredient> ingredients) {
        this.name = name;
        this.recipe = recipe;
        this.imageName = imageName;
        this.ingredients = ingredients;
    }

    public String getName() {
        return this.name;
    }

    public String getImageName() {
        return this.imageName;
    }

    public String getRecipe() {
        return recipe;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }



}
