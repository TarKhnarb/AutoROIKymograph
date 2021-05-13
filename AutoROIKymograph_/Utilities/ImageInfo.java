package Utilities;

import ij.ImagePlus;

public class ImageInfo{

    public final double duration;
    public final int nbFrames;
    public final int width;
    public final int height;
    public final int type;

    public ImageInfo(ImagePlus image){

        this.duration = 0;
        this.nbFrames = image.getNFrames();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.type = image.getType();
    }
}
