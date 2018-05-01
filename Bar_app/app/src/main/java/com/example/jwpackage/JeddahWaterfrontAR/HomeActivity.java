package com.example.jwpackage.JeddahWaterfrontAR;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (common.isConnectedToInternet(getBaseContext())) {
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    finish();
                }
            }
        },4000);

    }
}
