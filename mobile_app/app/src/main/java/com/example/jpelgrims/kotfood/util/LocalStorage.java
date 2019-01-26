package com.example.jpelgrims.kotfood.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.*;

import com.example.jpelgrims.kotfood.models.Meal;
import com.example.jpelgrims.kotfood.models.ShoppingList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.jpelgrims.kotfood.util.Util.tryParseInt;

public class LocalStorage {

    Activity activity;
    Context context;

    public LocalStorage(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public List<Meal> getMeals() {
        SharedPreferences settings = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString("meals", null);
        Type type = new TypeToken<List<Meal>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void storeMeals(List<Meal> meals) {
        SharedPreferences settings = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Editor prefsEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(meals);
        prefsEditor.putString("meals", json);
        prefsEditor.apply();
    }

    public ShoppingList getShoppingList() {
        SharedPreferences settings = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString("shoppingList", null);
        return gson.fromJson(json, ShoppingList.class);
    }

    public void storeShoppinglist(ShoppingList shoppingList) {
        SharedPreferences settings = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Editor prefsEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(shoppingList);
        prefsEditor.putString("shoppingList", json);
        prefsEditor.apply();
    }

    public void resetLastUpdate() {
        SharedPreferences settings = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("lastUpdate", new Date().getTime());
        editor.apply();
    }

    public Date getLastUpdate() {
        SharedPreferences settings = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        long date_nr = settings.getLong("lastUpdate", -1);
        return date_nr != -1? new Date(date_nr): null;
    }
}
