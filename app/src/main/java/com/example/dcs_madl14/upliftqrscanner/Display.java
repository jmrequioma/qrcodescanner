package com.example.dcs_madl14.upliftqrscanner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import android.content.Intent;

/**
 * Created by dcs-madl14 on 10/17/17.
 */

public class Display extends Activity {
    Button btnScan;
    TextView status_value;
    TextView passenger_value;
    TextView porter_value;
    TextView via_value;
    TextView destination_value;
    TextView bus_value;
    TextView loading_value;
    TextView weight_value;
    TextView fee_value;
    TextView number_value;
    String qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //status_value.setText("hellow");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        status_value = (TextView) findViewById(R.id.porter_value);
        passenger_value = (TextView) findViewById(R.id.passenger_value);
        porter_value = (TextView) findViewById(R.id.porter_value);
        via_value = (TextView) findViewById(R.id.via_value);
        destination_value = (TextView) findViewById(R.id.destination_value);
        bus_value = (TextView) findViewById(R.id.bus_value);
        loading_value = (TextView) findViewById(R.id.loading_value);
        weight_value = (TextView) findViewById(R.id.weight_value);
        fee_value = (TextView) findViewById(R.id.fee_value);
        number_value = (TextView) findViewById(R.id.destination_value);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent i = getIntent();
        qrCode = i.getStringExtra("status");

    }

    @Override
    protected void onStart() {
        super.onStart();
        new requestPassenger().execute();
        new requestStatus().execute();
    }

    private class requestPassenger extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                String username = "cmsc131";
                String password = "Dh0ngFh3l";
                String authString = username + ":" + password;
                URL url = new URL("https://baggage-loading-system.herokuapp.com/passenger/" + qrCode);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String encoding = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
                try {
                    InputStream content = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((content)));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("Display", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            String passenger = displayName(response);
            String fee = displayFee(response);
            String number = displayNumber(response);
            passenger_value.setText(passenger);
            fee_value.setText(fee);
            number_value.setText(number);
        }

    }
    private String displayStatus(String response) {
        String status = "";
        try {
            JSONArray array = new JSONArray(response);
            for(int i = 0; i < array.length(); i++) {
                JSONObject explrObject = array.getJSONObject(i);
                status += explrObject.getString("status") + ", ";
            }
            Log.i("hello", status);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return status;
    }

    private String displayName(String response) {
        String passenger = "";
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            passenger = object.getString("first_name");
            passenger = passenger + " " + object.getString("last_name");
            Log.i("hello", passenger);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return passenger;
    }

    private String displayPorter(String response) {
        String passenger = "";
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            passenger = object.getString("first_name");
            passenger = passenger + " " + object.getString("last_name");
            Log.i("hello", passenger);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return passenger;
    }

    private String displayFee(String response) {
        String fee = "";
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            fee = object.getString("fee");
            Log.i("hello", fee);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return fee;
    }

    private String displayNumber(String response) {
        String cNum = "";
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            cNum = object.getString("contact_number");
            Log.i("hello", cNum);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return cNum;
    }

    private class requestStatus extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                String username = "cmsc131";
                String password = "Dh0ngFh3l";
                String authString = username + ":" + password;
                URL url = new URL("https://baggage-loading-system.herokuapp.com/passenger/" + qrCode + "/baggage");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String encoding = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
                try {
                    InputStream content = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((content)));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("Display", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            String status = displayStatus(response);
            status_value.setText(status);
        }
    }
}
