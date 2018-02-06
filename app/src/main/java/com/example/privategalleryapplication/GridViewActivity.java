package com.example.privategalleryapplication;

import adapter.GridViewImageAdapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.BoolRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;


import adapter.GridViewImageAdapter;
import PrivateGalleryApplication.helper.AppConstant;
import PrivateGalleryApplication.helper.Utils;
import PrivateGalleryApplication.helper.RealPathUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.example.privategalleryapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GridViewActivity extends AppCompatActivity {

    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private ArrayList<Integer> imagePvt=new ArrayList<Integer>();
    private ArrayList<ImageHolder> imagesHolder;
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private String dir;
    private String dirPath;
    private String parent_dir;
    private String parentDirPath;
    private boolean pvt=false;
    private boolean lockToggle=true;
    private static String algorithm = "AES";
    static SecretKey yourKey = null;
    public static final String MY_PREFS_NAME = "AppFile";
    public ProgressBar progressBar;
    private int countSecret;
    private int countShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        //dir = getIntent().getStringExtra("DIRECTORY_ID");
        dir="1";
        //parent_dir = getIntent().getStringExtra("PARENT_DIRECTORY_ID");
        if(getIntent().getStringExtra("PRIVATE").equals("0")){
            pvt=false;
        }
        else{
            pvt=true;
        }
        dirPath=Environment.getExternalStorageDirectory() + "/MyGallery/" + dir;
        //parentDirPath=Environment.getExternalStorageDirectory() + "/MyGallery/" + parent_dir;
        Boolean resDir = createDirIfNotExists();

        //Get key from shared preferences file
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        byte[] decodedKey     = Base64.decode(prefs.getString("key", null), Base64.DEFAULT);
        yourKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        //get image holder object from shared preference file
        imagesHolder=getImageHolderList();
        Log.d("aretrive",Integer.toString(imagesHolder.size()));

        gridView = (GridView) findViewById(R.id.grid_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);


        utils = new Utils(this);//        showPopup((this.findViewById(android.R.id.content));

        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());
        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        boolean empty;
        imagePaths = utils.getFilePaths(dir);
//            //empty=utils.isEmpty(dir,parent_dir);
//        }
//        else{
//            for
//            //empty=utils.empty;
//        }
        countSecret=0;
        countShared=0;
        for(int i=0;i<imagesHolder.size();i++){
            if(imagesHolder.get(i).isPrivate){
                countSecret++;
            }else{
                countShared++;
            }
        }

        if((pvt==true&&countSecret==0&&countShared==0)||(pvt==false&&countShared==0)){
            empty=true;
        }else {
            empty=false;
        }


        if(empty==true){
            LinearLayout linearLayout= new LinearLayout(this);
            ImageView imageView = new ImageView(this);
            Bitmap notAvailable= BitmapFactory.decodeResource(getResources(),R.drawable.not_available);
            imageView.setImageBitmap(notAvailable);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(params);



            LinearLayout ll=(LinearLayout) findViewById(R.id.LL);
            ll.removeView(progressBar);
            linearLayout.addView(progressBar);
            linearLayout.addView(imageView);

            setContentView(linearLayout);

            if(pvt==true){
                invalidateOptionsMenu();
            }
//            Intent intent=new Intent(this,TempActivity.class);
//            startActivity(intent);

        }
        else{
            // Initilizing Grid View
            InitializeGridLayout();

            // loading all image paths from SD card
            if(pvt==true) {
                for(int i=0;i<imagesHolder.size();i++){
                    imagePaths.add(imagesHolder.get(i).path);
                }
//                int inter=imagePaths.size();
//                for (int i = 0; i < inter; i++) {
//                    imagePvt.add(1);
//                }
//                imagePaths.addAll(utils.getFilePaths(parent_dir));
//                for (int i=inter;i<imagePaths.size(); i++) {
//                    imagePvt.add(0);
//                }
                //add move to private option
                invalidateOptionsMenu();
                //pvt=true;
            }else{
                for(int i=0;i<imagesHolder.size();i++){
                    if(!imagesHolder.get(i).isPrivate){
                        imagePaths.add(imagesHolder.get(i).path);
                    }
                }
//                for (int i = 0; i < imagePaths.size(); i++) {
//                    imagePvt.add(0);
//                }
            }


            // Gridview adapter
            setAdapter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(pvt==false) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }else {
            getMenuInflater().inflate(R.menu.menu_main2, menu);
            return true;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(pvt==true) {
            MenuItem checkable = menu.findItem(R.id.menu_lock);
            checkable.setChecked(lockToggle);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_import:
                selectFromGallery(); return true;
            case R.id.menu_export:
                exportToGallery(adapter); return true;
            case R.id.menu_change_password:
                changePublicPassword(); return true;
            case R.id.menu_change_public_password:
                changePublicPassword();return true;
            case R.id.menu_change_private_password:
                changePublicPassword();return true;
            case R.id.menu_private:
                moveToSecret(adapter);return true;
            case R.id.menu_public:
                moveToShared(adapter);return true;
            case R.id.menu_lock:
                toggleLock(item);return true;
            case R.id.menu_sign_out:
                signOut();return true;
            case R.id.menu_help:
                launchHelp();return true;
            case R.id.menu_delete:
                deleteImages(adapter);return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void InitializeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    public boolean createDirIfNotExists() {
        boolean ret = true;

        File file = new File(dirPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        File noMedia = new File(dirPath+"/.nomedia");
        if (!noMedia.exists()) {
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    private void selectFromGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),2);
        Intent intent = new Intent(this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Here","YO!");
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<com.darsh.multipleimageselect.models.Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            try {
                new MovingFilesTask(images, Boolean.valueOf(pvt), GridViewActivity.this, yourKey, dirPath, columnWidth).execute();
            } catch (Exception e) {
                Log.e("MYAPP", "exception", e);
            }
        }
    }

    public void moveFile(String inputPath, String inputFile, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        Toast.makeText(getApplicationContext(),outputPath,
                Toast.LENGTH_LONG).show();
        try {
            File dir = new File (outputPath);
            in = new FileInputStream(inputPath +"/"+ inputFile);
            out = new FileOutputStream(outputPath +"/"+ inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath +"/"+ inputFile).delete();
        }
        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public void moveFileDecrypted(String inputPath, String inputFile, String outputPath) {
        try {
            //read file
            byte[] fileToSave=readFile(inputPath,"Original"+inputFile);

            //saving file
            File file = new File(outputPath + "/"+inputFile);
            Log.d("writeTo",outputPath + "/"+inputFile);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] filesBytes = decodeFileEncrypted(yourKey, fileToSave);
            Log.d("fileSize",Integer.toString(filesBytes.length));

            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new File(inputPath +"/"+ inputFile).delete();
        new File(inputPath +"/Original"+ inputFile).delete();
    }

    public void moveFileEncrypted(String inputPath, String inputFile, String outputPath) {
        try {
            //read file
            byte[] fileToSave=readFile(inputPath,inputFile);

            //saving file
            File file = new File(outputPath + File.separator,"Original"+inputFile);
            File fileThumbnail = new File(outputPath + File.separator,inputFile);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            BufferedOutputStream bos2 = new BufferedOutputStream(new FileOutputStream(fileThumbnail));
            byte[] filesBytes = encodeFile(yourKey, fileToSave);
            byte[] thumbnailBytes=getThumbnail(fileToSave);
            thumbnailBytes=encodeFile(yourKey,thumbnailBytes);

            bos.write(filesBytes);
            bos2.write(thumbnailBytes);
            bos.flush();
            bos2.flush();
            bos.close();
            bos2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new File(inputPath +"/"+ inputFile).delete();
    }

    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        Log.d("Key", Base64.encodeToString(yourKey.getEncoded(), Base64.DEFAULT));
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        Log.d("Sizee",Integer.toString(encrypted.length));
        return encrypted;
    }

    public byte[] getThumbnail(byte[] file) {
        ThumbnailUtils tUtil = new ThumbnailUtils();
        Bitmap bitmap = BitmapFactory.decodeByteArray(file, 0, file.length);
        Log.d("Height",Integer.toString(columnWidth));
        bitmap = tUtil.extractThumbnail(bitmap,columnWidth, columnWidth);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public byte[] readFile(String path,String fileName) {
        byte[] contents = null;

        File file = new File(path+File.separator,fileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public String extractFileName(String path){
        File f = new File(path);
        return f.getName();
    }

    public String extractPathOnly(String path){
        int pos = path.lastIndexOf("/");
        if (pos == -1) {
            return path;
        }
        return path.substring(0, pos);

    }

    private void exportToGallery(GridViewImageAdapter adapter){
        for(int i=0;i<adapter._toPrivate.size();i++){
            String inputFile=extractFileName(adapter._toPrivate.get(i));
            Log.d("fileName",inputFile);
            ImageHolder image=findImageHolder(inputFile);
            if(image==null){
                Toast.makeText(getApplicationContext(), "The selected image(s) cannot be moved!",
                        Toast.LENGTH_LONG).show();
            }
            else {
//                if (!image.isPrivate  && pvt) {
//                    Log.d("Opt","1");
//                    moveFileDecrypted(parentDirPath, inputFile,image.source);
//                } else if (image.isPrivate && pvt) {
//                    Log.d("Opt","2");
//                    moveFileDecrypted(dirPath, inputFile, image.source);
//                } else if (!image.isPrivate && !pvt) {
//                    Log.d("Opt","3");
//                    moveFileDecrypted(dirPath, inputFile, image.source);
//                }
                moveFileDecrypted(dirPath, inputFile, image.source);
                //check all other loops that need to be removed
                //imagePvt.set(imagePaths.indexOf(adapter._toPrivate.get(i)), 1);
            }
            deleteImageHolder(image);
        }
        adapter._toPrivate.clear();
        startActivity(getIntent());
        finish();
    }

    public ImageHolder findImageHolder(String fileName){
        Log.d("yo",Integer.toString(imagesHolder.size()));
        for(int i=0;i<imagesHolder.size();i++){
            Log.d("names",imagesHolder.get(i).fileName);
            Log.d("req",fileName);
            if(imagesHolder.get(i).fileName.equals(fileName)){
                return imagesHolder.get(i);
            }
        }
        return null;
    }

    public void updateImageHolderField(String fileName,ImageHolder image){
        Log.d("yo",Integer.toString(imagesHolder.size()));
        for(int i=0;i<imagesHolder.size();i++){
            if(imagesHolder.get(i).fileName.equals(fileName)){
                imagesHolder.set(i,image);
            }
        }
    }

    public void deleteImageHolder(ImageHolder imageHolder){
        for(int i=0;i<imagesHolder.size();i++){
            if(imagesHolder.get(i).fileName.equals(imageHolder.fileName)){
                Log.d("Deletes","YO");
                imagesHolder.remove(i);
            }
        }
        saveImageHolderList(imagesHolder);
    }

    private void moveToSecret(GridViewImageAdapter adapter){
        for(int i=0;i<adapter._toPrivate.size();i++){
            String inputFile=extractFileName(adapter._toPrivate.get(i));
//            moveFile(parentDirPath,inputFile,dirPath);
            //imagePvt.set(imagePaths.indexOf(adapter._toPrivate.get(i)),1);
            ImageHolder image=findImageHolder(inputFile);
            image.isPrivate=true;
            updateImageHolderField(inputFile,image);
        }
        saveImageHolderList(imagesHolder);
        adapter._toPrivate.clear();
        startActivity(getIntent());
        finish();
    }

    private void deleteImages(GridViewImageAdapter adapter){
        for(int i=0;i<adapter._toPrivate.size();i++){
            Log.d("called","1");
            Log.d("size",Integer.toString(imagesHolder.size()));
            String inputFile=extractFileName(adapter._toPrivate.get(i));
            ImageHolder imgHolder=findImageHolder(inputFile);
            deleteImageHolder(imgHolder);
            String path; String path2;
            if(imgHolder.isPrivate && pvt){
                Log.d("called","2");
                path=dirPath+"/"+inputFile;
                path2=dirPath+"/Original"+inputFile;
            }
            else if(!imgHolder.isPrivate && pvt){
                Log.d("called","3");
                path=parentDirPath+"/"+inputFile;
                path2=parentDirPath+"/Original"+inputFile;
                Log.d("path",path);
            }
            else{
                Log.d("called","4");
                path=dirPath+"/"+inputFile;
                path2=dirPath+"/Original"+inputFile;
            }
            Log.d("Image to delete",inputFile);
            deleteFileFunc(path);
            deleteFileFunc(path2);
            //delete images from imagePvt
        }
        adapter._toPrivate.clear();
        startActivity(getIntent());
        finish();
    }

    private void moveToShared(GridViewImageAdapter adapter){
        new AlertDialog.Builder(this)
                .setTitle("Move To Shared")
                .setMessage("Do you really want to move the selected photos to Shared Gallery?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for(int i=0;i<GridViewActivity.this.adapter._toPrivate.size();i++){
                            String inputFile=extractFileName(GridViewActivity.this.adapter._toPrivate.get(i));
//                            moveFile(dirPath,inputFile,parentDirPath);
//                            //imagePvt.set(imagePaths.indexOf(GridViewActivity.this.adapter._toPrivate.get(i)),0);
                            ImageHolder image=findImageHolder(inputFile);
                            image.isPrivate=false;
                            updateImageHolderField(inputFile,image);
                        }
                        saveImageHolderList(imagesHolder);
                        GridViewActivity.this.adapter._toPrivate.clear();
                        startActivity(getIntent());
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void changePublicPassword(){
        Intent intent=new Intent(this,ChangePassword.class);
        if(pvt==true){
            intent.putExtra("TYPE", "Private");
        }
        else{
            intent.putExtra("TYPE", "Public");
        }
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void deleteFileFunc(String path){
        File fdelete=new File(path);
        fdelete.delete();
    }

    public static byte[] decodeFileEncrypted(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    public void toggleLock(MenuItem item){
        lockToggle=!item.isChecked();
        item.setChecked(lockToggle);
        setAdapter();
    }

    public void signOut(){
        Intent intent=new Intent(this,LoginScreen.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void launchHelp(){
        Intent intent=new Intent(this,Help.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void setAdapter(){
        Bitmap lock= BitmapFactory.decodeResource(getResources(),R.drawable.lock);
        adapter = new GridViewImageAdapter(GridViewActivity.this,columnWidth,lock,lockToggle,yourKey,imagesHolder,countSecret,countShared,pvt);

        // setting grid view adapter
        gridView.setAdapter(adapter);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void saveImageHolderList(ArrayList<ImageHolder> imageHolderList){
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(imageHolderList);
        editor.putString("ImageHolderList", json);
        editor.commit();
        editor.apply();
    }

    private ArrayList<ImageHolder> getImageHolderList(){
        ArrayList<ImageHolder> images=null;
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("ImageHolderList", "");
        if(!json.equals("")){
            Type type = new TypeToken<ArrayList<ImageHolder>>(){}.getType();
            images= gson.fromJson(json, type);
        }
        else{
            images=new ArrayList<ImageHolder>();
        }
        return images;
    }


    public class ImageHolder{
        public boolean isPrivate;
        public String fileName;
        public String source;
        public String path;

        ImageHolder(String _fileName,String _source,String _path,boolean _isPrivate){
            fileName=_fileName;
            source=_source;
            isPrivate=_isPrivate;
            path=_path;
        }
    }

    public class MovingFilesTask extends AsyncTask<String,Integer,String> {
        GridViewActivity gridActivity;
        SecretKey yourKey = null;
        Boolean pvt = false;
        GridViewActivity gridViewActivity = null;
        String dirPath = null;
        private String algorithm = "AES";
        int columnWidth;
        ArrayList<com.darsh.multipleimageselect.models.Image> images;

        public MovingFilesTask(ArrayList<com.darsh.multipleimageselect.models.Image> _images, Boolean _pvt, GridViewActivity _gridActivity, SecretKey _yourKey, String _dirPath, int _columnWidth) {
            super();
            yourKey = _yourKey;
            images = _images;
            pvt = _pvt;
            gridActivity = _gridActivity;
            yourKey = _yourKey;
            dirPath = _dirPath;
            columnWidth = _columnWidth;
        }

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0, l = images.size(); i < l; i++) {
                String realpath;
                try {
                    realpath=images.get(i).path;
                    String inputFile = extractFileName(realpath);
                    Log.d("Here", "YO!");
                    moveFileEncrypted(extractPathOnly(realpath), inputFile, dirPath);
                    publishProgress(10*i/images.size());
                    ImageHolder img = new ImageHolder(inputFile,extractPathOnly(realpath),dirPath+"/"+inputFile, pvt);
                    Log.d("sourcePath",realpath);
                    Log.d("sourceFile",inputFile);
                    imagesHolder.add(img);
                } catch (Exception e) {
                }
            }
            Log.d("Before saving",Integer.toString(imagesHolder.size()));
            saveImageHolderList(imagesHolder);
            return "Completed";
        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "The files have been moved!",
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            setContentView(R.layout.activity_grid_view);
            //Intent i=getIntent();
            //i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(getIntent());
            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("Values",Integer.toString(values[0]));
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(gridView.VISIBLE);
        }
    }
}