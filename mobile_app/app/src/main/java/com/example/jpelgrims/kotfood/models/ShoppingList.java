package com.example.jpelgrims.kotfood.models;

import java.util.List;

public class ShoppingList {

    private List<Ingredient> items;
    private Double estimatedPrice;
    private boolean shared = false;

    public ShoppingList(List<Ingredient> items, Double estimatedPrice, boolean shared) {
        this.items = items;
        this.estimatedPrice = estimatedPrice;
        this.shared = shared;
    }

    public Double getEstimatedPrice() {
        return this.estimatedPrice;
    }

    public List<Ingredient> getItems() {
        return this.items;
    }

    public boolean isShared() {
        return shared;
    }
}
