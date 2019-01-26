package com.example.jpelgrims.kotfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.jpelgrims.kotfood.util.APIClient;

import static com.example.jpelgrims.kotfood.util.Util.haveNetworkConnection;
import static com.example.jpelgrims.kotfood.util.Util.showSuccessPopup;
import static com.example.jpelgrims.kotfood.util.Util.tryParseInt;

public class SettingsActivity extends AppCompatActivity {

    private EditText budgetEdit;
    private EditText calorieEdit;
    private EditText portionsEdit;

    private APIClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        client = new APIClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        budgetEdit = findViewById(R.id.budgetEdit);
        calorieEdit = findViewById(R.id.calorieEdit);
        portionsEdit = findViewById(R.id.portionsEdit);

        loadData();

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    saveData();
                }

            }
        };

        budgetEdit.setOnFocusChangeListener(listener);
        calorieEdit.setOnFocusChangeListener(listener);
        portionsEdit.setOnFocusChangeListener(listener);
    }

    private void loadData() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        Integer budget = settings.getInt("budget", 50);
        budgetEdit.setText(budget.toString());

        Integer calorie_limit = settings.getInt("calorie_limit", 1000);
        calorieEdit.setText(calorie_limit.toString());

        Integer portions = settings.getInt("portions", 1);
        portionsEdit.setText(portions.toString());
    }

    private void saveData() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        String budget = budgetEdit.getText().toString();
        editor.putInt("budget", tryParseInt(budget));

        String calorie_limit = calorieEdit.getText().toString();
        editor.putInt("calorie_limit", tryParseInt(calorie_limit));

        String portions = portionsEdit.getText().toString();
        editor.putInt("portions", tryParseInt(portions));

        editor.apply();
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
        saveData();
        int id = item.getItemId();

        if (id == R.id.meals_menu_item) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.settings_menu_item) {
            // Already on this activity
        } else if (id == R.id.shoppinglist_menu_item) {
            Intent intent = new Intent(this, ShoppingListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        return true;
    }

    public void onShowQRCode(View view) {
        Intent intent = new Intent(this, ShowQRCodeActivity.class);
        startActivity(intent);
    }

    public void onScanQRCode(View view) {
        Intent intent = new Intent(this, ScanQRCodeActivity.class);
        startActivity(intent);
    }

    public void onNewMealPlan(View view) {
        if (!haveNetworkConnection(this)) {
            showSuccessPopup(this, "Er kon geen nieuw maaltijdplan aangemaakt worden. Probeer het later nog eens opnieuw.");
        }
        client.updateMeals(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
            }

            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }
}
