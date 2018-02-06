package com.example.privategalleryapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import adapter.MyDBHandler;

public class ChangePassword extends AppCompatActivity {

    private String type;
    TextView heading;
    EditText userInputOld;
    EditText userInputNew;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        type = getIntent().getStringExtra("TYPE");
        heading = (TextView) findViewById(R.id.textView8);
        if(type.equals("Private")){
            heading.setText("Change Secret Password");
        }
        dbHandler = new MyDBHandler(this, null, null, 1);
    }

    @Override
    public void onBackPressed()
    {

        // super.onBackPressed(); // Comment this super call to avoid calling finish() or fragmentmanager's backstack pop operation.
    }

    public void changeButton(View view) {
        userInputOld=(EditText) findViewById(R.id.editText4);
        String oldPassword=userInputOld.getText().toString();

        userInputNew=(EditText) findViewById(R.id.editText5);
        String newPassword=userInputNew.getText().toString();

        if(dbHandler.isUser(oldPassword)){
            dbHandler.updateUser(newPassword,oldPassword);
            Intent intent=new Intent(this,LoginScreen.class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "You have entered an incorrect current password!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
