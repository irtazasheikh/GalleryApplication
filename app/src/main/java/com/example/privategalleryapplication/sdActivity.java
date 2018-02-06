package com.example.privategalleryapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.privategalleryapplication.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

public class sdActivity extends Activity implements MediaScannerConnection.MediaScannerConnectionClient {
    public String[] allFiles;
    private String SCAN_PATH;
    private static final String FILE_TYPE = "image/*";

    private MediaScannerConnection conn;

    /**
     * Called when the activity is first created.
     */
    private String dir;
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Toast.makeText(getApplicationContext(), "CREATED!!",
//                Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        dir = getIntent().getStringExtra("DIRECTORY_ID");

        File folder = new File(Environment.getExternalStorageDirectory() + "/MyGallery/" + dir);
        allFiles = folder.list();
        //   uriAllFiles= new Uri[allFiles.length];
        for (int i = 0; i < allFiles.length; i++) {
            Log.d("all file path" + i, allFiles[i] + allFiles.length);
        }
        //  Uri uri= Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()+"/yourfoldername/"+allFiles[0]));
        SCAN_PATH = Environment.getExternalStorageDirectory() + "/MyGallery/" + dir + "/"+ allFiles[0];
        Log.d("SCAN PATH", "Scan Path " + SCAN_PATH);
        startScan();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void startScan() {
        Log.d("Connected", "success" + conn);
        if (conn != null) {
            conn.disconnect();
        }
        conn = new MediaScannerConnection(this, this);
        conn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        Log.d("onMediaScannerConnected", "success" + conn);
        conn.scanFile(SCAN_PATH, FILE_TYPE);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        try {
            Log.d("onScanCompleted", uri + "success" + conn);
            if (uri != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        } finally {
            conn.disconnect();
            conn = null;
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "sd Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.privategalleryapplication/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "sd Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.privategalleryapplication/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }
}