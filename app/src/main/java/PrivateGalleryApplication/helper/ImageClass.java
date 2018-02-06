package PrivateGalleryApplication.helper;

/**
 * Created by irtaz_000 on 7/18/2017.
 */
public class ImageClass {
    private int isPrivate;
    private String path;

    ImageClass(){

    }

    ImageClass(String _path,int pvt){
        isPrivate=pvt;
        path=_path;
    }

    String get_path(){
        return path;
    }

    int get_private(){
        return isPrivate;
    }
}
