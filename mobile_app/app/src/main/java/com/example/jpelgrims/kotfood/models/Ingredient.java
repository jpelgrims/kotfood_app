package com.example.jpelgrims.kotfood.models;

public class Ingredient {

    private String name;
    private String measurement;
    private Integer amount;
    private String unit;
    private boolean purchased = false;
    private double price;

    public Ingredient(String name, String measurement, String unit, Integer amount, Double price) {
        this.name = name;
        this.measurement = measurement;
        this.amount = amount;
        this.unit = unit;
        this.price = price;
    }

    public Ingredient(String name, String measurement, String unit, Integer amount, Double price, boolean purchased) {
        this(name, measurement, unit, amount, price);
        this.purchased = purchased;
    }

    public String getName() {
        return this.name;
    }

    public String getMeasurement() {
        return this.measurement;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public String getUnit() {
        return this.unit;
    }

    public boolean isPurchased() {return this.purchased;}

    public Double getPrice() {
        return this.price;
    }
}
