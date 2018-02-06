package com.example.privategalleryapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TempActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

            LinearLayout linearLayout= new LinearLayout(this);
            ImageView imageView = new ImageView(this);
            Bitmap notAvailable= BitmapFactory.decodeResource(getResources(),R.drawable.not_available);
            imageView.setImageBitmap(notAvailable);
            linearLayout.addView(imageView);
            setContentView(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
