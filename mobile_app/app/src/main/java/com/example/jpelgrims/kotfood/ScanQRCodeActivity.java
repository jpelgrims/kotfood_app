package com.example.jpelgrims.kotfood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.jpelgrims.kotfood.util.APIClient;
import com.example.jpelgrims.kotfood.util.Util;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.example.jpelgrims.kotfood.util.Util.getUniquePhoneIdentifier;

public class ScanQRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private APIClient client;

    // Look at https://github.com/dm77/barcodescanner/blob/master/zxing-sample/src/main/java/me/dm7/barcodescanner/zxing/sample/FullScannerActivity.java
    // In case this doesn't work as required

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scan_qrcode);

        client = new APIClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add scanner to layout
        mScannerView = new ZXingScannerView(this);
        ViewGroup layout = (ViewGroup) findViewById(R.id.scanLayout);
        layout.addView(mScannerView);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                1);

        mScannerView.startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission denied to use your camera.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Prints scan results
        System.out.println("result: " + rawResult.getText());
        // Prints the scan format (qrcode, pdf417 etc.)
        System.out.println("result: " + rawResult.getBarcodeFormat().toString());
        //If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
        Intent intent = new Intent();
        intent.putExtra("qr_code", rawResult.getText());

        client.shareShoppingList(rawResult.getText());

        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }
}
