package PrivateGalleryApplication.helper;

/**
 * Created by irtaz_000 on 7/3/2017.
 */
import java.util.Arrays;
import java.util.List;

public class AppConstant {

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // SD card image directory
    public static final String PHOTO_ALBUM = "/storage/emulated/0/DCIM/Facebook";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
            "png");
}