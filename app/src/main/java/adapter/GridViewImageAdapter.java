package adapter;

/**
 * Created by irtaz_000 on 7/3/2017.
 */
import com.example.privategalleryapplication.GridViewActivity;
import com.example.privategalleryapplication.R;
import com.example.privategalleryapplication.fullscreen_view;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import PrivateGalleryApplication.helper.CheckableImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import PrivateGalleryApplication.helper.CheckableImageView;
import PrivateGalleryApplication.helper.ImageClass;

public class GridViewImageAdapter extends BaseAdapter {

    private Activity _activity;
    public int imageWidth;
    public ArrayList<String> _toPrivate = new ArrayList<String>();
    public ArrayList<Integer> imagePvt = new ArrayList<Integer>();
    private Bitmap lock;
    private boolean lockToggle;
    private static String algorithm = "AES";
    public static SecretKey yourKey;
    ArrayList<GridViewActivity.ImageHolder> imagesHolder;
    ArrayList<GridViewActivity.ImageHolder> sharedImages=new ArrayList<GridViewActivity.ImageHolder>();
    private int countSecret;
    private int countShared;
    private boolean pvt;

    public GridViewImageAdapter(Activity activity,int imageWidth,Bitmap _lock,boolean _lockToggle,SecretKey _yourKey,
                                ArrayList<GridViewActivity.ImageHolder> _imagesHolder,int _countSecret,int _countShared,boolean _pvt) {
        this._activity = activity;
        this.imageWidth = imageWidth;
        lock=_lock;
        lockToggle=_lockToggle;
        yourKey=_yourKey;
        imagesHolder=_imagesHolder;
        countSecret=_countSecret;
        countShared=_countShared;
        pvt=_pvt;
        setupSharedImages();
    }

    @Override
    public int getCount() {
        if(pvt){
            Log.d("count",Integer.toString(countSecret+countShared));
            return countSecret+countShared;
        }else{
            Log.d("count",Integer.toString(countShared));
            return countShared;
        }
    }

    @Override
    public Object getItem(int position) {
        if(pvt){
            return imagesHolder.get(position).path;
        }else{
            return sharedImages.get(position).path;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(_activity);
        CheckBox checkbox = new CheckBox(_activity);
        checkbox.setClickable(false);
        RelativeLayout layout;
        if (convertView == null) {
            layout = new RelativeLayout(_activity);
        } else {
            layout = (RelativeLayout) convertView;
        }

        // get screen dimensions
        String imagePath;
        Log.d("pos",Integer.toString(position));
        if(pvt){
            imagePath=imagesHolder.get(position).path;
        }else{
            imagePath=sharedImages.get(position).path;

        }
        Bitmap image= decodeFile(imagePath, imageWidth,imageWidth);

        //add an overlay if private
        if(pvt) {
            if (imagesHolder.get(position).isPrivate) {
                image = overlay(image, lock);
            }
        }
        else{
            if (sharedImages.get(position).isPrivate) {
                image = overlay(image, lock);
            }
        }
//        if(imagePvt.get(position)==1){
//            image=overlay(image,lock);
//        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        imageView.setImageBitmap(image);

        // image view click listener
        imageView.setOnClickListener(new OnImageClickListener(position));
        imageView.setOnLongClickListener(new MyLongClickListener(imageView,imagePath,_toPrivate,checkbox));
        checkbox.setOnClickListener(new MyCheckBoxClickListener(imageView,imagePath,_toPrivate,checkbox));

        RelativeLayout.LayoutParams checkparam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        checkparam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, imageView.getId());
        checkparam.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
        checkbox.setLayoutParams(checkparam);

        layout.addView(imageView);
        layout.addView(checkbox);

        return layout;
    }

    private class MyLongClickListener implements View.OnLongClickListener
    {

        ImageView img;
        String path;
        ArrayList<String> toPrivate;
        CheckBox checkBox;
        public MyLongClickListener(ImageView _img,String _path,ArrayList<String> _toPrivate,CheckBox _checkBox) {
            img=_img;
            path=_path;
            toPrivate=_toPrivate;
            checkBox=_checkBox;
        }

        @Override
        public boolean onLongClick(View v)
        {
            Log.d("update",path);
//            img.toggle();
            img.setImageAlpha(75);
            _toPrivate.add(path);
            checkBox.setChecked(true);
            return true;
        }

    }

    private class MyCheckBoxClickListener implements View.OnClickListener
    {

        ImageView img;
        String path;
        ArrayList<String> toPrivate;
        CheckBox checkBox;
        public MyCheckBoxClickListener(ImageView _img,String _path,ArrayList<String> _toPrivate,CheckBox _checkBox) {
            img=_img;
            path=_path;
            toPrivate=_toPrivate;
            checkBox=_checkBox;
        }

        @Override
        public void onClick(View v)
        {
            Log.d("update",path);
//            img.toggle();
            img.setImageAlpha(75);
            _toPrivate.add(path);
            checkBox.setChecked(true);
        }

    }

    class OnImageClickListener implements OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, fullscreen_view.class);
            i.putExtra("position", _postion);
            i.putExtra("pvt",Boolean.toString(pvt));
            //_toPrivate.clear();
            _activity.startActivity(i);
        }
    }
    /*
     * Resizing image size
     */
    public Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {
            //File f = new File(filePath);
            byte[] contents=readFile(extractPathOnly(filePath),extractFileName(filePath));
            contents=decodeFileEncrypted(yourKey,contents);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            //BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            BitmapFactory.decodeByteArray(contents,0,contents.length,o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            //return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return BitmapFactory.decodeByteArray(contents,0,contents.length,o2);
//        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
//        Matrix matrix = new Matrix();
//        int scale=1;
//        while (bmp2.getWidth() / scale / 2 >= imageWidth
//                && bmp2.getHeight() / scale / 2 >= imageWidth)
//            scale *= 2;
//        matrix.setScale(1/scale,1/scale);
//        matrix.setTranslate(imageWidth/2,imageWidth/2);
        //        if(bmp1.getWidth()<bmp1.getHeight()) {
        //editBMP = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth() / 2, bmp1.getHeight() / 2, false);
//        }
//        else {
//            editBMP = Bitmap.createScaledBitmap(bmp2, bmp1.getHeight() / 2, bmp1.getHeight() / 2, false);
//        }
        if(lockToggle==true) {
            Bitmap editBMP;
            Bitmap editBMP2;
            editBMP = Bitmap.createScaledBitmap(bmp1, imageWidth, imageWidth, false);
            editBMP2 = Bitmap.createScaledBitmap(bmp2, imageWidth / 4, imageWidth / 4, false);

            Bitmap bmOverlay = Bitmap.createBitmap(imageWidth, imageWidth, editBMP.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(editBMP, new Matrix(), null);
            canvas.drawBitmap(editBMP2, new Matrix(), null);
            return bmOverlay;
        }
        else{
            return bmp1;
        }
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

    public static String extractFileName(String path){
        File f = new File(path);
        return f.getName();
    }

    public static byte[] decodeFileEncrypted(SecretKey yourKey, byte[] fileData)
            throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(fileData);
    }

    public static String extractPathOnly(String path){
        int pos = path.lastIndexOf("/");
        if (pos == -1) {
            return path;
        }
        return path.substring(0, pos);

    }

    public GridViewActivity.ImageHolder findImageHolder(String fileName){
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

    public void setupSharedImages() {
        for (int i = 0; i < imagesHolder.size(); i++) {
            if (!imagesHolder.get(i).isPrivate) {
                sharedImages.add(imagesHolder.get(i));
            }
        }
    }
}

