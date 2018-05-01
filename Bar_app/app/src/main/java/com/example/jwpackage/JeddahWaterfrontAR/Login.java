package com.example.jwpackage.JeddahWaterfrontAR;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class Login extends AppCompatActivity {

    EditText UseremailEt, PasswordEt;
    LinearLayout rootLayout;
    Context context;

    public Login(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UseremailEt = findViewById(R.id.etUserName);
        PasswordEt =  findViewById(R.id.etPassword);
        rootLayout = findViewById(R.id.rootLayout);
        context = getBaseContext();
    }

    public void onReg(View view) {
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }

    public void OnLogin(View view) {
        String user_Email = UseremailEt.getText().toString();
        String password = PasswordEt.getText().toString();
        String type = "login";

        if (!user_Email.isEmpty() && !password.isEmpty()) {

            if (user_Email.contains("@")) {

                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(type, user_Email, password);
            }else{
                Toast.makeText(Login.this, "Error!! Wrong input", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login.this, "Error!! Empty fields", Toast.LENGTH_SHORT).show();
        }
    }


}
