package com.example.jwpackage.JeddahWaterfrontAR;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  {

    public Button loginBttn, guestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginBttn = findViewById(R.id.loginbutn0);
        guestBtn = findViewById(R.id.gustbutn);

        if (common.isConnectedToInternet(getBaseContext())) {
            loginBttn.setEnabled(true);
            guestBtn.setEnabled(true);
            common.sp = getSharedPreferences(ConstantFields.userInfo, Context.MODE_PRIVATE);
            common.editor = common.sp.edit();
            checkLogin();
        } else {
            loginBttn.setEnabled(false);
            guestBtn.setEnabled(false);
        }

    }

    public void onLogin(View view) {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    public void asGest(View view) {
        Intent i = new Intent(this, BarActivity.class);
        startActivity(i);
    }

    public void checkLogin() {
        if (common.sp.getBoolean(ConstantFields.userIsLoggedIn, false) == true) {
            Intent intent = new Intent(getBaseContext(), BarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }




}