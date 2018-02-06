package com.example.privategalleryapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import adapter.MyDBHandler;

public class ForgotPassword extends AppCompatActivity {

    EditText superPassword;
    TextView sharedPassword;
    TextView secretPassword;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        superPassword=(EditText) findViewById(R.id.superPassword);
        sharedPassword=(TextView) findViewById(R.id.sharedPassword);
        secretPassword=(TextView) findViewById(R.id.secretPassword);
        dbHandler = new MyDBHandler(this, null, null, 1);
    }

    public void forgotPasswordBtn(View view){
        Log.d("in","1");
        if(superPassword.getText().toString().equals("nyu123")){
            Log.d("in","2");
            ArrayList<String> passwords=dbHandler.getPasswords();
            Log.d("pass",passwords.get(0));
            sharedPassword.setText(passwords.get(0));
            secretPassword.setText(passwords.get(1));
        }
    }
}
