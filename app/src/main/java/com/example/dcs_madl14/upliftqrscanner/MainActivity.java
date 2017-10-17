package com.example.dcs_madl14.upliftqrscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by dcs-madl14 on 10/10/17.
 */

public class MainActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    Button CameraPermissionButton;
    private final int CAMERA_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        CameraPermissionButton = (Button) findViewById(R.id.CameraPermissionButton);
        CameraPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission(Manifest.permission.ACCESS_FINE_LOCATION, CAMERA_REQUEST_CODE);
            }
        });
        */

    }

    private void askPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // don't have permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            // have permission
            Toast.makeText(this, "Permission is already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    public void onClick(View v) {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        //Intent i = new Intent(MainActivity.this, Display.class);
        //startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }

    private void continueScan() {
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void handleResult(Result result) {
        // we can use this to fetch data from database
        String qrCodeVal = result.getText();
        Log.v("handleResult", qrCodeVal);
        Intent i = new Intent(MainActivity.this, Display.class);
        // pass qrCode value
        i.putExtra("status", qrCodeVal);
        startActivity(i);
    }
}
