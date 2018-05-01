package com.example.jwpackage.JeddahWaterfrontAR;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    EditText eTname, eTemail, eTpass, eTconpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        eTname = findViewById(R.id.eTName);
        eTemail = findViewById(R.id.eTEmail);
        eTpass = findViewById(R.id.eTPass);
        eTpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        eTconpass = findViewById(R.id.eTConpass);
        eTconpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void OnReg(View view) {
        String sName = eTname.getText().toString();
        String sEmail = eTemail.getText().toString();
        String sPass = eTpass.getText().toString();
        String sConPass = eTconpass.getText().toString();
        String type = "Register";

        if (!sName.isEmpty() && !sEmail.isEmpty() && !sPass.isEmpty() && !sConPass.isEmpty()) {
            if (sEmail.contains("@") && sPass.equals(sConPass)) {
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(type, sName, sEmail, sPass);
            } else {
                Toast.makeText(Register.this, "Error!! Wrong Email or Password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Register.this, "Error!! Empty fields", Toast.LENGTH_SHORT).show();
        }
    }
}
