package com.example.privategalleryapplication;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import adapter.MyDBHandler;

public class LoginScreen extends AppCompatActivity {
    EditText userInput;
    MyDBHandler dbHandler;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Request for access to storage device
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        //Setup connection to database
        dbHandler = new MyDBHandler(this, null, null, 1);
        if(dbHandler.isPresent()==0){
            launchNewAccount(this.findViewById(android.R.id.content));
        }

        forgotPassword=(TextView) findViewById(R.id.textView17);
        forgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                launchForgotPassword();
            }
        });
    }

    public void loginButton(View view){
//        Intent intent=new Intent(this,FeaturesScreen.class);
//        startActivity(intent);
        userInput=(EditText) findViewById(R.id.editText);
        String password=userInput.getText().toString();
        if(dbHandler.isPresent()==1) {
            String directory = dbHandler.Directory(password);
            if(directory!=""){
                if(dbHandler.typeAccount(password).equals("Secret")) {
                    launchGridViewActivity(1);
                }
                else{
                    launchGridViewActivity(0);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "You have entered an incorrect password",
                        Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "The password that you have entered is incorrect!",
                    Toast.LENGTH_LONG).show();

        }
    }

    public void launchNewAccount(View view){
        Intent intent=new Intent(this,NewAccount.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void launchGridViewActivity(int _pvt){
        Intent intent=new Intent(this,GridViewActivity.class);
//        intent.putExtra("DIRECTORY_ID", directory);
        intent.putExtra("PRIVATE", Integer.toString(_pvt));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void launchForgotPassword()
    {
        Intent intent=new Intent(this,ForgotPassword.class);
        startActivity(intent);
    }
}

