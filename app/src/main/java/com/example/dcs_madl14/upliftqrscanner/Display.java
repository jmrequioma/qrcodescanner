package com.example.dcs_madl14.upliftqrscanner;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.ProgressDialog;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.*;
import android.content.Intent;

/**
 * Created by dcs-madl14 on 10/17/17.
 */

public class Display extends Activity {
    ImageButton btnRefresh;
    ProgressDialog dialog;
    Button btnScan;
    TextView status_label;
    TextView passenger_label;
    TextView via_label;
    TextView destination_label;
    TextView bus_label;
    TextView loading_label;
    //TextView weight_label;
    //TextView fee_label;
    TextView status_value;
    TextView passenger_value;
    TextView via_value;
    TextView destination_value;
    TextView bus_value;
    TextView loading_value;
    //TextView weight_value;
    //TextView fee_value;

    ImageView check;
    String qrCode;
    String id;

    final String CURR = "Php";
    final String WEIGHT_UNIT = "kg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //status_value.setText("hellow");
        // link textviews to the their respective ids from the xml file
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        dialog = new ProgressDialog(Display.this);
        status_label = (TextView) findViewById(R.id.status_label);
        status_value = (TextView) findViewById(R.id.status_value);
        passenger_label = (TextView) findViewById(R.id.passenger_label);
        via_label = (TextView) findViewById(R.id.via_label);
        destination_label = (TextView) findViewById(R.id.destination_label);
        bus_label = (TextView) findViewById(R.id.bus_label);
        loading_label = (TextView) findViewById(R.id.loading_label);
        //weight_label = (TextView) findViewById(R.id.weight_label);
        //fee_label = (TextView) findViewById(R.id.fee_label);
        passenger_value = (TextView) findViewById(R.id.passenger_value);
        via_value = (TextView) findViewById(R.id.via_value);
        destination_value = (TextView) findViewById(R.id.destination_value);
        bus_value = (TextView) findViewById(R.id.bus_value);
        loading_value = (TextView) findViewById(R.id.loading_value);
        //weight_value = (TextView) findViewById(R.id.weight_value);
        //fee_value = (TextView) findViewById(R.id.fee_value);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        check = (ImageView) findViewById(R.id.check);
        check.setVisibility(View.INVISIBLE);
        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
        Typeface montserratBold = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Bold.otf");
        Typeface montserratReg = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Regular.otf");
        status_label.setTypeface(montserratBold);
        status_value.setTypeface(montserratBold);
        passenger_label.setTypeface(montserratReg);
        via_label.setTypeface(montserratReg);
        destination_label.setTypeface(montserratReg);
        bus_label.setTypeface(montserratReg);
        loading_label.setTypeface(montserratReg);
        //weight_label.setTypeface(montserratReg);
        //fee_label.setTypeface(montserratReg);
        passenger_value.setTypeface(montserratBold);
        via_value.setTypeface(montserratBold);
        destination_value.setTypeface(montserratBold);
        bus_value.setTypeface(montserratBold);
        loading_value.setTypeface(montserratBold);
        //weight_value.setTypeface(montserratBold);
        //fee_value.setTypeface(montserratBold);
        btnScan.setTypeface(montserratReg);
        Intent i = getIntent();
        qrCode = i.getStringExtra("status");
        // execute here so it's faster
        try {
            new RequestPassenger().execute();
            new RequestStatus().execute();
            new RequestLoadingBay().execute();
        } catch (Exception e) {
            Intent intent = new Intent(Display.this, ErrorDisplay.class);
            startActivity(intent);
        }
        // this sets timer to 10 seconds so we can close the activity
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }, 30000);   // 30 seconds
    }
    @Override
    protected void onStart() {
        super.onStart();
        autoUpdate();
        //new RequestPassenger().execute();
        //new RequestStatus().execute();
        //new RequestLoadingBay().execute();
    }
    
    // retrieves passenger information
    private class RequestPassenger extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                String username = "cmsc131";
                String password = "Dh0ngFh3l";
                String authString = username + ":" + password;
                URL url = new URL("https://baggage-loading-system.herokuapp.com/api/passenger/" + qrCode);
                Log.i("qr code:", qrCode);
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
                } catch (FileNotFoundException fnfe) {
                    return null;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("Display", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            //dialog.setMessage("Processing...");
            //dialog.show();
        }

        @Override
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            } else {
                String passenger = displayName(response);
                //String fee = CURR + " " + displayFee(response);
                String lBay = displayLoadingBay(response);
                //String weight = displayWeight(response) + " " + WEIGHT_UNIT;
                id = lBay;
                passenger_value.setText(passenger);
                //fee_value.setText(fee);
                //loading_value.setText(lBay);
                //weight_value.setText(weight);
                dialog.dismiss();
            }
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
        }

    }

    // (String) -> String
    // returns baggage status value from the baggage status value found in the database
    private String displayStatus(String response) {
        int loadedCnt = 0;
        int unloadedCnt = 0;
        String status = "";
        try {
            JSONArray array = new JSONArray(response);
            for(int i = 0; i < array.length(); i++) {
                JSONObject explrObject = array.getJSONObject(i);
                status = explrObject.getString("status");
                if (status.equals("LOADED")) {
                    loadedCnt++;
                } else {
                    unloadedCnt++;
                }
            }
            if (loadedCnt == loadedCnt + unloadedCnt) {
                check.setVisibility(View.VISIBLE);
            }
            //Log.i("hello", status);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return String.valueOf(loadedCnt) + "/" + String.valueOf(loadedCnt + unloadedCnt);
    }

    // (String) -> String
    // returns name value from the first name and last name values found in the database
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

    // (String) -> String
    // returns fee value from the fee value found in the database
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

    // (String) -> String
    // returns loading bay value from the loading bay value found in the database
    private String displayLoadingBay(String response) {
        String lBay = "";
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            lBay = object.getString("loading_bay_id");
            Log.i("hello", lBay);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return lBay;
    }

    // (String) -> String
    // returns weight value from the weight value found in the database
    private String displayWeight(String response) {
        String weight = "";
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            weight = object.getString("baggage_weight");
            Log.i("hello", weight);
            //JSONArray photos = new JSONTokener(response).nextValue();
            //JSONArray.
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
        return weight;
    }

    // requests for baggage status
    private class RequestStatus extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                String username = "cmsc131";
                String password = "Dh0ngFh3l";
                String authString = username + ":" + password;
                URL url = new URL("https://baggage-loading-system.herokuapp.com/api/passenger/" + qrCode + "/baggage");
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
                } catch (Exception e) {
                    return null;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {

                Log.e("Display", e.getMessage(), e);
                //Intent i = new Intent(Display.this, ErrorDisplay.class);
                //startActivity(i);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";

            } else {
                String status = displayStatus(response);
                status_value.setText(status);
            }
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

        }
    }

    // requests for loading bay status
    private class RequestLoadingBay extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                String username = "cmsc131";
                String password = "Dh0ngFh3l";
                String authString = username + ":" + password;
                URL url = new URL("https://baggage-loading-system.herokuapp.com/api/loading-bay/" + id);
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
                } catch (Exception e) {
                    return null;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("Display", e.getMessage(), e);
                //Intent i = new Intent(Display.this, ErrorDisplay.class);
                //startActivity(i);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
                dialog.dismiss();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                Intent i = new Intent(Display.this, ErrorDisplay.class);
                startActivity(i);
            } else {
                displayLoadingInfo(response);
                dialog.dismiss();
            }
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Processing...");
            dialog.show();
        }
    }

    // (String) -> void
    // sets text values to the values retrieved from the database
    private void displayLoadingInfo(String response) {
        int id;
        String via, destination, bus, lBayName;
        try {
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            via = object.getString("via");
            destination = object.getString("destination");
            bus = object.getString("bus_company");
            lBayName = object.getString("loading_bay_name");
            via_value.setText(via);
            destination_value.setText(destination);
            bus_value.setText(bus);
            loading_value.setText(lBayName);
        } catch (JSONException e) {
            // Appropriate error handling code
            Log.e("Display", e.getMessage(), e);
        }
    }

    // (View) -> void
    // retrieves data from database again
    public void refreshStat(View v) {
        new RequestPassenger().execute();
        new RequestStatus().execute();
        new RequestLoadingBay().execute();
    }

    // (void) -> void
    // updates the bagggage status
    public void autoUpdate() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            RequestStatus requestStatus = new RequestStatus();
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            requestStatus.execute();
                            Log.i("INFO", "auto update here!!!!");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 3000); //execute in every 3 s
    }
}
