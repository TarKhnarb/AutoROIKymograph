package Required;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.ArrayList;

public class PreShredder{

        /*************
         * Variables *
         *************/
    private ImagePlus baseImage;
    private float[][] processImage;
    private final Range<Double> intensity;
    private final int subSearchLgth;
    private final ImageInfo baseImgInfo;

        /***************
         * Constructor *
         ***************/
    public PreShredder(ImagePlus image, double minIntensity, double maxIntensity, int subSearchLgth, ImageInfo imageInfo){

        this.baseImage = image.duplicate();
        this.processImage = image.duplicate().getProcessor().getFloatArray();
        this.intensity = new Range<>(minIntensity, maxIntensity);
        this.subSearchLgth = subSearchLgth;
        this.baseImgInfo = imageInfo;

        preprocess();
    }

        /*************
         * Available *
         *************/
    private static class Available{

            /*************
             * Variables *
             *************/
        public final int x;
        public final int y;
        public final float value;

            /***************
             * Constructor *
             ***************/
        public Available(int x, int y, float value){

            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

        /**************
         * Preprocess *
         **************/
    private void preprocess(){

        float[][] pixel = this.baseImage.getProcessor().getFloatArray();
        ArrayList<Available> tmp = new ArrayList<>();
        float tmpValue;
        int lastId;
        for(int j = 0; j < this.baseImgInfo.height; ++j){

            lastId = 0;
            for(int i = 0; i < this.baseImgInfo.width; ++i){

                this.processImage[i][j] = 0.0f;
                tmpValue = pixel[i][j];
                if(this.intensity.belongToItself(tmpValue)){

                    if(tmp.isEmpty()){

                        tmp.add(new Available(i, j, tmpValue));
                    }
                    else{

                        if((i - lastId) >= this.subSearchLgth){

                            tmp.add(new Available(i, j, tmpValue));
                        }
                        else if(pixel[lastId][j] < tmpValue){

                            tmp.set(tmp.size() - 1, new Available(i, j, tmpValue));
                        }
                    }

                    lastId = i;
                }
            }

            if(!tmp.isEmpty()){

                for(Available a : tmp){

                    this.processImage[a.x][j] = 128.0f;
                }

                tmp.clear();
            }
        }
    }

        /**************
         * GetProcess *
         **************/
    public ImagePlus getProcess(){

        ImageProcessor tmp = this.baseImage.getProcessor();
        tmp.setFloatArray(this.processImage);
        return new ImagePlus("processed", tmp);
    }
}
