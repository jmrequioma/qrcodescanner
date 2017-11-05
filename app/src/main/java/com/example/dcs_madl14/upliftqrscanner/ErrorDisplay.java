package com.example.dcs_madl14.upliftqrscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Joshua Requioma on 11/5/2017.
 */

public class ErrorDisplay extends Activity {
    TextView errorMsg;
    Button tryAgain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_display);
        errorMsg = (TextView) findViewById(R.id.error_msg);
        tryAgain = (Button) findViewById(R.id.try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Typeface montserratBold = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Bold.otf");
        Typeface montserratReg = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Regular.otf");
        errorMsg.setTypeface(montserratBold);
        tryAgain.setTypeface(montserratReg);
    }
}
