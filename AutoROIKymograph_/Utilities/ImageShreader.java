package Utilities;

import ij.ImagePlus;

public class ImageShreader{

    private final ImagePlus image;      // Image to process
    private final Range intensity;      // Intensity range of valid pixels
    private final int searchLength;     // // Length in micrometer of the search interval around a pixel with coordinate (x,y)

    public ImageShreader(ImagePlus image, int minIntensity, int maxIntensity, int searchLength){

        this.image = image;
        this.intensity = new Range(minIntensity, maxIntensity);
        this.searchLength = searchLength;
    }
}
