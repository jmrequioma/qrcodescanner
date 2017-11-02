package com.example.dcs_madl14.upliftqrscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Joshua Requioma on 10/31/2017.
 */

public class Scanner extends Activity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String qrCodeVal = result.getText();
        Log.v("handleResult", qrCodeVal);
        Intent i = new Intent(Scanner.this, Display.class);
        // pass qrCode value
        i.putExtra("status", qrCodeVal);
        startActivity(i);
    }
}
