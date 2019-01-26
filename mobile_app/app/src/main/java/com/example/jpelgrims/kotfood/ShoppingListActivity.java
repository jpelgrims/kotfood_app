package com.example.jpelgrims.kotfood;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.jpelgrims.kotfood.models.Ingredient;
import com.example.jpelgrims.kotfood.models.ShoppingList;
import com.example.jpelgrims.kotfood.util.APIClient;
import com.example.jpelgrims.kotfood.util.LocalStorage;
import com.example.jpelgrims.kotfood.util.Util;

public class ShoppingListActivity extends AppCompatActivity {

    APIClient client;
    LocalStorage localStorage;
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        client = new APIClient(this);
        localStorage = new LocalStorage(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshShoppingList();

        swipeLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    public void refreshShoppingList() {
        boolean isInternetUp = Util.haveNetworkConnection(this);
        if (!isInternetUp) {
            View errorView = findViewById(R.id.errorView);
            errorView.setVisibility(View.VISIBLE);

            TextView errorTitle = findViewById(R.id.errorTitle);
            TextView errorMessage = findViewById(R.id.errorMessage);
            errorTitle.setText("Server onbereikbaar");
            errorMessage.setText("Er kon geen verbinding gemaakt worden met de backend-server. Deze verbinding is nodig om de boodschappenlijst up-to-date te houden.");

            // Load shoppinglist from local storage
            ShoppingList shoppingList = localStorage.getShoppingList();
            populateShoppingList(shoppingList, false);
        } else {
            View errorView = findViewById(R.id.errorView);
            errorView.setVisibility(View.GONE);
            client.updateShoppingList(true);
        }
    }

    public void refreshContent() {
        refreshShoppingList();
        swipeLayout.setRefreshing(false);
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
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.settings_menu_item) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.shoppinglist_menu_item) {
            // Already on this activity
        }

        return true;
    }

    private View newView(Ingredient ingr, boolean isInternetUp) {
        final Ingredient ingredient = ingr;
        LinearLayout checkItem = new LinearLayout(this);
        checkItem.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox checkBox = new CheckBox(this);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 5, 0, 5);
        checkBox.setLayoutParams(layoutParams);

        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {getResources().getColor(R.color.positiveButtonBg), getResources().getColor(R.color.gray)};
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));

        if (!isInternetUp) {
            checkBox.setEnabled(false);
        }

        if (ingredient.isPurchased()) {
            checkBox.setChecked(ingredient.isPurchased());
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                System.out.println("checked " + isChecked);
                client.setStatus(ingredient.getName(), isChecked);
            }
        });

        TextView textView = new TextView(this);
        ViewGroup.LayoutParams layoutParamsText = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 7, 15, 5);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        textView.setText(ingredient.getName() + ", " +ingredient.getAmount() + " " + ingredient.getUnit());

        TextView textView2 = new TextView(this);
        ViewGroup.LayoutParams layoutParamsText2 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView2.setLayoutParams(layoutParamsText2);
        textView2.setGravity(Gravity.RIGHT);
        textView2.setPadding(15, 7, 40, 5);
        textView2.setTextSize(20);
        textView2.setTextColor(getResources().getColor(R.color.backgroundColorCard));
        textView2.setText("€" + ingredient.getPrice().toString());

        checkItem.addView(checkBox);
        checkItem.addView(textView);
        checkItem.addView(textView2);

        return checkItem;
    }

    public void populateShoppingList(ShoppingList shoppingList, boolean isInternetUp) {

        LinearLayout layout = findViewById(R.id.shoplist);
        layout.removeAllViews();
        // Get checkmark drawable
//        TypedValue value = new TypedValue();
//        this.getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
//        int checkMarkDrawableResId = value.resourceId;


        for(Ingredient ingredient: shoppingList.getItems()) {
            View view = newView(ingredient, isInternetUp);
            layout.addView(view);
        }

        findViewById(R.id.booschappenlijstTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.divider6).setVisibility(View.VISIBLE);
        findViewById(R.id.divider).setVisibility(View.VISIBLE);

        TextView totalPrice = findViewById(R.id.totalPrice);
        totalPrice.setText("Geschatte totaalprijs: €" + String.format("%.2f", shoppingList.getEstimatedPrice()));

        View view = findViewById(R.id.sharedMessage);
        if (shoppingList.isShared()) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

}
