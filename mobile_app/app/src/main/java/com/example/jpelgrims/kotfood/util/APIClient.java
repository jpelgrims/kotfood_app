package com.example.jpelgrims.kotfood.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jpelgrims.kotfood.MainActivity;
import com.example.jpelgrims.kotfood.R;
import com.example.jpelgrims.kotfood.ShoppingListActivity;
import com.example.jpelgrims.kotfood.models.Ingredient;
import com.example.jpelgrims.kotfood.models.Meal;
import com.example.jpelgrims.kotfood.models.ShoppingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.jpelgrims.kotfood.util.Util.getUniquePhoneIdentifier;
import static com.example.jpelgrims.kotfood.util.Util.showSuccessPopup;

public class APIClient {

    LocalStorage localStorage;
    RequestQueue requestQueue;
    Activity activity;
    Context context;

    String serverUrl = "http://mobiledev.jellepelgrims.com/api";
    //String serverUrl = "http://10.0.3.2:5000";

    public APIClient(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.localStorage = new LocalStorage(activity);
        requestQueue = Volley.newRequestQueue(context);
    }

    private String buildUrl() {
        String baseUrl = serverUrl + "/meal_plan";

        SharedPreferences settings = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Integer budget = settings.getInt("budget", 0);
        Integer calorie_limit = settings.getInt("calorie_limit", 0);
        Integer portions = settings.getInt("portions", 0);

        String uri = String.format("?id=%1$s&calorie_limit=%2$s&budget=%3$s&portions=%4$s",
                getUniquePhoneIdentifier(activity),
                calorie_limit.toString(),
                budget.toString(),
                portions.toString());

        return baseUrl + uri;
    }

    public void updateMeals(final boolean updateView) {
        // TODO host backend on actual server and change url
        String url = buildUrl();

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Meal> meals = APIClient.parseMeals(response);

                        if (meals != null) {
                            localStorage.storeMeals(meals);
                            localStorage.resetLastUpdate();

                            if (updateView) {
                                ((MainActivity) activity).fillCardviews(meals);
                            }

                            showSuccessPopup(activity, "Er werd een nieuw maaltijdplan aangemaakt!");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());

                        try {
                            View mealList = activity.findViewById(R.id.mealList);
                            mealList.setVisibility(View.GONE);

                            View errorView = activity.findViewById(R.id.errorView);
                            errorView.setVisibility(View.VISIBLE);

                            if (!Util.haveNetworkConnection(activity)) {
                                TextView errorTitle = activity.findViewById(R.id.errorTitle);
                                TextView errorMessage = activity.findViewById(R.id.errorMessage);
                                errorTitle.setText("Server onbereikbaar");
                                errorMessage.setText("Er kon geen verbinding gemaakt worden met de backend-server. Deze verbinding is nodig om een nieuw maaltijdplan te kunnen downloaden. Probeer het later nog eens opnieuw.");
                            }
                        } catch (Exception e) {
                            Log.e("Volley", e.toString());
                        }

                    }
                }
        );

        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }

    public void updateShoppingList(final boolean updateView) {
        // TODO host backend on actual server and change url
        String url = serverUrl + "/shopping_list?id=" + getUniquePhoneIdentifier(activity);

        JsonRequest arrReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ShoppingList shoppingList = APIClient.parseShoppingList(response);
                        localStorage.storeShoppinglist(shoppingList);

                        if (updateView) {
                            ((ShoppingListActivity) activity).populateShoppingList(shoppingList, true);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                        if (Util.haveNetworkConnection(activity)) {
                            View errorView = activity.findViewById(R.id.errorView);
                            errorView.setVisibility(View.VISIBLE);

                            TextView errorTitle = activity.findViewById(R.id.errorTitle);
                            TextView errorMessage = activity.findViewById(R.id.errorMessage);
                            errorTitle.setText("Server onbereikbaar");
                            errorMessage.setText("Er kon geen verbinding gemaakt worden met de backend-server. Deze verbinding is nodig om de boodschappenlijst up-to-date te houden.");

                            ShoppingList shoppingList = localStorage.getShoppingList();
                            if (shoppingList != null && updateView) {
                                ((ShoppingListActivity) activity).populateShoppingList(shoppingList, false);
                            }
                        }
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }

    public void setStatus(final String ingredientName, final boolean isChecked) {
        final Integer ingredientStatus = isChecked ? 1 : 0;
        System.out.println("status " + ingredientStatus);
        String url = serverUrl + "/purchase";
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Util.showSuccessPopup(activity, "De aankoop kon niet geregistreerd worden i.v.m. de ontbrekende serververbinding.");
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id",getUniquePhoneIdentifier(activity));
                params.put("ingredient_name",ingredientName);
                params.put("ingredient_status", ingredientStatus.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(sr);
    }

    public void shareShoppingList(final String generator_id) {
        String url = serverUrl + "/shopping_list";
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.showSuccessPopup(activity, "Jouw boodschappenlijst is nu gedeeld!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.showSuccessPopup(activity, "Er ging iets mis tijdens het delen van de boodschappenlijst. Probeer later nog eens opnieuw!");
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("generator_id",generator_id);
                params.put("scanner_id",getUniquePhoneIdentifier(activity));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(sr);
    }


    private static ArrayList<Meal> parseMeals(JSONArray response) {
        ArrayList<Meal> meals = new ArrayList<>();
        if (response.length() > 0) {

            for (int i = 0; i < response.length(); i++) {
                try {
                    // For each repo, add a new line to our repo list.
                    JSONObject jsonObj = response.getJSONObject(i);

                    String mealName = jsonObj.get("name").toString();
                    String mealRecipe = jsonObj.get("recipe").toString();
                    String mealImageName = jsonObj.get("image").toString();
                    List<Ingredient> mealIngredients = new ArrayList<>();

                    JSONArray json_ingredients = jsonObj.getJSONArray("ingredients");

                    for (int y = 0; y < json_ingredients.length(); y++) {
                        JSONObject ingredient = json_ingredients.getJSONObject(y);
                        String ingredientName = ingredient.getString("name");
                        String ingredientMeasurement = ingredient.getString("measurement");
                        Integer ingredientAmount = ingredient.getInt("amount");
                        Double ingredientPrice= ingredient.getDouble("price");

                        String ingredientUnit;
                        try {
                            ingredientUnit = ingredient.getString("unit");
                        } catch (JSONException e) {
                            ingredientUnit = "";
                        }

                        Ingredient meal_ingredient = new Ingredient(ingredientName, ingredientMeasurement, ingredientUnit, ingredientAmount, ingredientPrice);
                        mealIngredients.add(meal_ingredient);
                    }

                    Meal meal = new Meal(mealName, mealRecipe, mealImageName, mealIngredients);
                    meals.add(meal);

                    //addToRepoList(repoName, lastUpdated);
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                    // If there is an error then output this to the logs.
                    Log.e("Volley", "Invalid JSON Object.");
                }
            }
        }

        return meals;
    }

    private static ShoppingList parseShoppingList(JSONObject response) {
        List<Ingredient> shopping_list_items = new ArrayList<>();
        Double estimatedPrice= 0.0;
        Boolean shared = false;

        if (response.length() > 0) {
            try {
                shared = response.getInt("shared")==1;
            } catch (JSONException e) {
                shared=false;
            }

            try {
                estimatedPrice = response.getDouble("estimated_price");
            } catch (JSONException e) {
                estimatedPrice = 0.0;
            }

            try {
                JSONArray items = response.getJSONArray("items");
                System.out.println(items);

                for (int y = 0; y < items.length(); y++) {

                    JSONObject ingredient = items.getJSONObject(y);
                    String ingredientName = ingredient.getString("name");
                    String ingredientMeasurement = ingredient.getString("measurement");
                    Integer ingredientAmount = ingredient.getInt("amount");
                    boolean ingredientPurchased = ingredient.getInt("purchased")==1;
                    Double ingredientPrice= ingredient.getDouble("price");

                    String ingredientUnit;
                    try {
                        ingredientUnit = ingredient.getString("unit");
                    } catch (JSONException e) {
                        ingredientUnit = "";
                    }

                    Ingredient item = new Ingredient(ingredientName, ingredientMeasurement, ingredientUnit, ingredientAmount, ingredientPrice, ingredientPurchased);
                    shopping_list_items.add(item);
                }

            } catch (JSONException e) {
                // If there is an error then output this to the logs.
                Log.e("Volley", "Invalid JSON Object.");
            }
        }

        return new ShoppingList(shopping_list_items, estimatedPrice, shared);
    }

    public boolean isServerReachable(Context context) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(this.serverUrl);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

}
