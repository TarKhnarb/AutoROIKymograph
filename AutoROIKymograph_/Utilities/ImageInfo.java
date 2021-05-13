package Utilities;

import ij.ImagePlus;

public class ImageInfo{

    public final double time;
    public final int nbFrames;
    public final int width;
    public final int height;

    public ImageInfo(ImagePlus image){

        this.time = 0;
        this.nbFrames = image.getNFrames();
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
}
