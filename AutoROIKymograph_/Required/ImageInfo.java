package Required;

import ij.ImagePlus;

public class ImageInfo{

        /*************
         * Variables *
         *************/
    public final int nbFrames;
    public final int width;
    public final int height;
    public final int type;

        /***************
         * Constructor *
         ***************/
    public ImageInfo(ImagePlus image){

        this.nbFrames = image.getNFrames();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.type = image.getType();
    }
}
