package PrivateGalleryApplication.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class Utils {

    private Context _context;
    public boolean empty=false;

    // constructor
    public Utils(Context context) {
        this._context = context;
    }

    // Reading file paths from SDCard

    public boolean isEmpty(String dir,String dir2){
        boolean empty=false;
        File directory=new File(Environment.getExternalStorageDirectory()+"/MyGallery/"+dir);
        File directory2=new File(Environment.getExternalStorageDirectory()+"/MyGallery/"+dir2);

        if (directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            if (listFiles.length > 0) {
                if(listFiles.length==1 && IsNoMediaFile(listFiles[0].getAbsolutePath())){
                    if (directory2.isDirectory()) {
                        File[] listFiles2 = directory2.listFiles();
                        if (listFiles2.length > 0) {
                            if(listFiles2.length==1 && IsNoMediaFile(listFiles2[0].getAbsolutePath())){
                                empty=true;
                            }
                        }
                        else{
                            empty=true;
                        }
                    }
                }
            }
            else{
                if (directory2.isDirectory()) {
                    File[] listFiles2 = directory2.listFiles();
                    if (listFiles2.length > 0) {
                        if(listFiles2.length==1 && IsNoMediaFile(listFiles2[0].getAbsolutePath())){
                            empty=true;
                        }
                    }
                    else{
                        empty=true;
                    }
                }
            }
        }
        return empty;
    }

    public ArrayList<String> getFilePaths(String dir) {
        ArrayList<String> filePaths = new ArrayList<String>();

        File directory=new File(Environment.getExternalStorageDirectory()+"/MyGallery/"+dir);
        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count
            if (listFiles.length > 0) {
                if(listFiles.length==1 && IsNoMediaFile(listFiles[0].getAbsolutePath())){
                    Toast.makeText(
                            _context,"Album is empty. Please load some images in it !",
                            Toast.LENGTH_LONG).show();
                    empty=true;
                }
                else {
                    for (int i = 0; i < listFiles.length; i++) {

                        // get file path
                        String filePath = listFiles[i].getAbsolutePath();

                        // check for supported file extension
                        if (IsSupportedFile(filePath)) {
                            // Add image path to array list
                            filePaths.add(filePath);
                        }
                    }
                }
            } else {
                // image directory is empty
                empty=true;
            }

        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(_context);
            alert.setTitle("Error!");
            alert.setMessage(AppConstant.PHOTO_ALBUM
                    + " directory path is not valid! Please set the image directory name AppConstant.java class");
            alert.setPositiveButton("OK", null);
            alert.show();
        }

        return filePaths;
    }

    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (AppConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault()))){
            if(filePath.contains("Original")){
                return false;
            }
            else{
                return true;
            }
        }
        else
            return false;

    }

    private boolean IsNoMediaFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (ext.equals("nomedia"))
            return true;
        else
            return false;
    }

    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}