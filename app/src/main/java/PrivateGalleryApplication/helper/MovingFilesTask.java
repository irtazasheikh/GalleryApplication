package PrivateGalleryApplication.helper;

import android.app.Activity;
import PrivateGalleryApplication.helper.Utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.privategalleryapplication.GridViewActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MovingFilesTask extends AsyncTask<String,Boolean,Void> {
    GridViewActivity gridActivity;
    SecretKey yourKey=null;
    String realpath=null;
    Boolean pvt=false;
    GridViewActivity gridViewActivity=null;
    String dirPath=null;
    private static String algorithm = "AES";
    int columnWidth;

    public MovingFilesTask(String _realpath, Boolean _pvt, GridViewActivity _gridActivity, SecretKey _yourKey, String _dirPath,int _columnWidth){
        super();
        yourKey=_yourKey;
        realpath=_realpath;
        pvt=_pvt;
        gridActivity=_gridActivity;
        yourKey=_yourKey;
        dirPath=_dirPath;
        columnWidth=_columnWidth;
    }

    @Override
    protected Void doInBackground(String...params) {
        try {
            String inputFile = extractFileName(realpath);
            Log.d("Here","YO!");

            moveFileEncrypted(extractPathOnly(realpath), inputFile, dirPath);
        }catch (Exception e){
        }
        return null;
    }

//    @override
//    protected void onPostExecute(ImageHolder result) {
//        if(result!=null){
//            startActivity(gridActivity.getIntent());
//        }
//    }

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

    class ImageHolder{
        public boolean isPrivate;
        public String fileName;
        public String source;

        ImageHolder(String _fileName,String _source,boolean _isPrivate){
            fileName=_fileName;
            source=_source;
            isPrivate=_isPrivate;
        }
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
}