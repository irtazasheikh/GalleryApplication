package com.example.privategalleryapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FeaturesScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features_screen);
    }

    public void launch(View view){
        Intent intent=new Intent(this,GridViewActivity.class);
        startActivity(intent);
    }
}
