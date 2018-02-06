package com.example.privategalleryapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import PrivateGalleryApplication.helper.Utils;
import adapter.FullScreenImageAdapter;
import adapter.GridViewImageAdapter;


public class fullscreen_view extends AppCompatActivity {

    private Utils utils;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private String pvt;
    private ArrayList<GridViewActivity.ImageHolder> imagesHolder;
    public static final String MY_PREFS_NAME = "AppFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        utils = new Utils(getApplicationContext());

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        pvt=i.getStringExtra("pvt");
        getImageHolderList();
        ArrayList<String> paths=generatePaths();
        Log.d("paths",paths.get(0));
        Log.d("pp",Integer.toString(position));

        adapter = new FullScreenImageAdapter(fullscreen_view.this,
                paths);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }

    private void getImageHolderList(){
        ArrayList<GridViewActivity.ImageHolder> images=null;
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("ImageHolderList", "");
        if(!json.equals("")){
            Type type = new TypeToken<ArrayList<GridViewActivity.ImageHolder>>(){}.getType();
            images= gson.fromJson(json, type);
        }
        else{
            images=new ArrayList<GridViewActivity.ImageHolder>();
        }

        if(pvt.equals("true")){
            imagesHolder=images;
        }
        else{
            imagesHolder=new ArrayList<GridViewActivity.ImageHolder>();
            for (int i = 0; i < images.size(); i++) {
                if (!images.get(i).isPrivate) {
                    imagesHolder.add(images.get(i));
                }
            }
        }
    }

    private ArrayList<String> generatePaths(){
        ArrayList<String> paths=new ArrayList<String >();
        for(int i=0;i<imagesHolder.size();i++){
            String filename=GridViewImageAdapter.extractFileName(imagesHolder.get(i).path);
            String path=GridViewImageAdapter.extractPathOnly(imagesHolder.get(i).path);
            paths.add(path+"/Original"+filename);
        }
        return paths;
    }
}
