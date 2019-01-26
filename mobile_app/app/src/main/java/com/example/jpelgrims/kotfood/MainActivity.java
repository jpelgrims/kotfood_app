package com.example.jpelgrims.kotfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.jpelgrims.kotfood.models.Ingredient;
import com.example.jpelgrims.kotfood.models.Meal;
import com.example.jpelgrims.kotfood.util.APIClient;
import com.example.jpelgrims.kotfood.util.DownloadImageTask;
import com.example.jpelgrims.kotfood.util.LocalStorage;
import com.example.jpelgrims.kotfood.util.Util;

import org.json.JSONArray;

import static com.example.jpelgrims.kotfood.util.Util.getUniquePhoneIdentifier;


public class MainActivity extends AppCompatActivity {

    APIClient client;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.clear().commit();

        client = new APIClient(this);
        localStorage = new LocalStorage(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        populateMealList();
        showCurrentDay();
    }

    public void showCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        View view;

        switch (day) {
            case Calendar.MONDAY:
                findViewById(R.id.mondayTitle).performClick();
                view = findViewById(R.id.mondayText);
                view.getParent().requestChildFocus(view, view);
                break;
            case Calendar.TUESDAY:
                findViewById(R.id.tuesdayTitle).performClick();
                view = findViewById(R.id.tuesdayText);
                view.getParent().requestChildFocus(view, view);
                break;
            case Calendar.WEDNESDAY:
                findViewById(R.id.wednesdayTitle).performClick();
                view = findViewById(R.id.wednesdayText);
                view.getParent().requestChildFocus(view, view);
                break;
            case Calendar.THURSDAY:
                findViewById(R.id.thursdayTitle).performClick();
                view = findViewById(R.id.thursdayText);
                view.getParent().requestChildFocus(view, view);
                break;
            case Calendar.FRIDAY:
                findViewById(R.id.fridayTitle).performClick();
                view = findViewById(R.id.fridayText);
                view.getParent().requestChildFocus(view, view);
                break;
            case Calendar.SATURDAY:
                findViewById(R.id.saturdayTitle).performClick();
                view = findViewById(R.id.saturdayText);
                view.getParent().requestChildFocus(view, view);
                break;
            case Calendar.SUNDAY:
                findViewById(R.id.sundayTitle).performClick();
                view = findViewById(R.id.sundayText);
                view.getParent().requestChildFocus(view, view);
                break;
        }
    }

    public void fillCardviews(List<Meal> meals) {
        findViewById(R.id.mealList).setVisibility(View.VISIBLE);
        List<String> days = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");

        Resources res = getResources();


        for (int i=0; i < meals.size(); i++) {

            String day = days.get(i);
            Meal meal = meals.get(i);

            int nameId = res.getIdentifier(day+"Name", "id", getApplicationContext().getPackageName());
            TextView mealName = findViewById(nameId);
            mealName.setText(meal.getName());

            int imageId = res.getIdentifier(day+"Image", "id", getApplicationContext().getPackageName());
            ImageView mealImage = findViewById(imageId);
            if (!Util.haveNetworkConnection(this)) {
                mealImage.setVisibility(View.GONE);
            }
            new DownloadImageTask(mealImage)
                    .execute("http://mobiledev.jellepelgrims.com/img/" + meal.getImageName());

            int ingredientsId = res.getIdentifier(day+"Ingredients", "id", getApplicationContext().getPackageName());
            TextView mealIngredients = findViewById(ingredientsId);

            List<Ingredient> ingredients = meal.getIngredients();
            StringBuilder ingredientsText = new StringBuilder();

            for(Ingredient ingredient: ingredients) {
                ingredientsText.append(ingredient.getName());
                ingredientsText.append(" (");
                ingredientsText.append(ingredient.getAmount());

                String unit = ingredient.getUnit();
                if (!unit.equals("")){
                    ingredientsText.append(" " + ingredient.getUnit());
                }
                ingredientsText.append("), ");
            }
            ingredientsText.delete(ingredientsText.length()-2, ingredientsText.length()-1);

            mealIngredients.setText(ingredientsText.toString());

            int preparationId = res.getIdentifier(day+"Preparation", "id", getApplicationContext().getPackageName());
            TextView mealPreparation = findViewById(preparationId);
            mealPreparation.setText(meal.getRecipe());
        }
    }

    public void onCardviewClick(View v) {

        // Hide all cardviews
        findViewById(R.id.mondayText).setVisibility(View.GONE);
        findViewById(R.id.tuesdayText).setVisibility(View.GONE);
        findViewById(R.id.wednesdayText).setVisibility(View.GONE);
        findViewById(R.id.thursdayText).setVisibility(View.GONE);
        findViewById(R.id.fridayText).setVisibility(View.GONE);
        findViewById(R.id.saturdayText).setVisibility(View.GONE);
        findViewById(R.id.sundayText).setVisibility(View.GONE);

        // Show selected cardview
        TextView text;
        switch(v.getId()) {
            case R.id.mondayTitle:
                findViewById(R.id.mondayText).setVisibility(View.VISIBLE);
                break;
            case R.id.tuesdayTitle:
                findViewById(R.id.tuesdayText).setVisibility(View.VISIBLE);
                break;
            case R.id.wednesdayTitle:
                findViewById(R.id.wednesdayText).setVisibility(View.VISIBLE);
                break;
            case R.id.thursdayTitle:
                findViewById(R.id.thursdayText).setVisibility(View.VISIBLE);
                break;
            case R.id.fridayTitle:
                findViewById(R.id.fridayText).setVisibility(View.VISIBLE);
                break;
            case R.id.saturdayTitle:
                findViewById(R.id.saturdayText).setVisibility(View.VISIBLE);
                break;
            case R.id.sundayTitle:
                findViewById(R.id.sundayText).setVisibility(View.VISIBLE);
                break;
        }

        v.getParent().requestChildFocus(v,v);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.meals_menu_item) {
            // Already on this activity
        } else if (id == R.id.settings_menu_item) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.shoppinglist_menu_item) {
            Intent intent = new Intent(this, ShoppingListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        return true;
    }


    private void populateMealList() {
        Date lastUpdate = localStorage.getLastUpdate();

        if (lastUpdate == null) {
            client.updateMeals(true);
        }
        else if(Util.getDaysSince(lastUpdate) > 7) {
            client.updateMeals(true);
        } else {
            List<Meal> meals = localStorage.getMeals();

            if (meals == null || meals.isEmpty()) {
                View mealList = findViewById(R.id.mealList);
                mealList.setVisibility(View.GONE);

                View errorView = findViewById(R.id.errorView);
                errorView.setVisibility(View.VISIBLE);

                if (Util.haveNetworkConnection(this)) {
                    TextView errorTitle = findViewById(R.id.errorTitle);
                    TextView errorMessage = findViewById(R.id.errorMessage);
                    errorTitle.setText("Server onbereikbaar");
                    errorMessage.setText("Er kon geen verbinding gemaakt worden met de backend-server. Deze verbinding is nodig om een nieuw maaltijdplan te kunnen downloaden. Probeer het later nog eens opnieuw.");
                }
            } else {
                fillCardviews(meals);
            }
        }
    }
}
