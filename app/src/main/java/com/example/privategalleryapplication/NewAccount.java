package com.example.privategalleryapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import adapter.MyDBHandler;

public class NewAccount extends AppCompatActivity {

    EditText userInput;
    EditText userInput2;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        dbHandler = new MyDBHandler(this, null, null, 1);
    }

    public void createPassButton(View view){
        userInput=(EditText) findViewById(R.id.editText2);
        userInput2=(EditText) findViewById(R.id.editText3);
        User user=new User(userInput.getText().toString(),"Shared");
        User user2=new User(userInput2.getText().toString(),"Secret");
        dbHandler.addUser(user);
        dbHandler.addUser(user2);

        //adding password to sharedPreference
        try {
            SecretKey key = generateKey();
            String encodedKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            String MY_PREFS_NAME = "AppFile";
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("key",encodedKey);
            editor.apply();
        }catch (Exception e){
            Log.e("MYAPP", "exception", e);
        }
        launchLoginScreen(view);
    }

    public void launchLoginScreen(View view){
        Intent intent=new Intent(this,LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }


    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey yourKey = keyGenerator.generateKey();
        return yourKey;
    }

    public void sharedPasswordInfo(View view){
        Toast.makeText(getApplicationContext(), "Shared Password will be used to access the Shared Gallery " +
                "where you can access images that you want to share with others but behind a password",
                Toast.LENGTH_LONG).show();
    }

    public void secretPasswordInfo(View view){
        Toast.makeText(getApplicationContext(), "Secret Password will be used to access the Secret Gallery " +
                        "where you can access images that you want to keep only to yourself behind a password",
                Toast.LENGTH_LONG).show();
    }
}

