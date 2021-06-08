package Required;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.ArrayList;

public class ROIOptimizer{

        /*************
         * Variables *
         *************/
    private final ImagePlus baseImage;
    private final float[][] processImageLR;
    private final float[][] processImageRL;
    private final Range<Double> intensity;
    private final int subSearchLgth;
    private final ImageInfo baseImgInfo;

        /***************
         * Constructor *
         ***************/
    public ROIOptimizer(ImagePlus image, double minIntensity, double maxIntensity, int subSearchLgth, ImageInfo imageInfo){

        this.baseImage = image;
        this.processImageLR = image.duplicate().getProcessor().getFloatArray();
        this.processImageRL = image.duplicate().getProcessor().getFloatArray();
        this.intensity = new Range<>(minIntensity, maxIntensity);
        this.subSearchLgth = subSearchLgth;
        this.baseImgInfo = imageInfo;

        preprocessRL();
        preprocessLR();
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

        /****************
         * PreprocessLR *
         ****************/
    private void preprocessLR(){

        float[][] pixel = this.baseImage.getProcessor().duplicate().getFloatArray();
        ArrayList<Available> tmp = new ArrayList<>();
        float tmpValue;
        int lastId;
        for(int j = 0; j < this.baseImgInfo.height; ++j){

            lastId = 0;
            for(int i = 0; i < this.baseImgInfo.width; ++i){

                this.processImageLR[i][j] = 0.0f;
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

                    this.processImageLR[a.x][a.y] = a.value;
                }

                tmp.clear();
            }
        }
    }

        /**************
         * Preprocess *
         **************/
    private void preprocessRL(){

        float[][] pixel = this.baseImage.getProcessor().duplicate().getFloatArray();
        ArrayList<Available> tmp = new ArrayList<>();
        float tmpValue;
        int lastId;
        int tmpI;
        for(int j = 0; j < this.baseImgInfo.height; ++j){

            lastId = this.baseImgInfo.width - 1;
            for(int i = 0; i < this.baseImgInfo.width; ++i){

                tmpI = this.baseImgInfo.width - 1 - i;
                this.processImageRL[tmpI][j] = 0.0f;
                tmpValue = pixel[tmpI][j];
                if(this.intensity.belongToItself(tmpValue)){

                    if(tmp.isEmpty()){

                        tmp.add(new Available(tmpI, j, tmpValue));
                    }
                    else{

                        if((lastId - tmpI) >= this.subSearchLgth){

                            tmp.add(new Available(tmpI, j, tmpValue));
                        }
                        else if(pixel[lastId][j] < tmpValue){

                            tmp.set(tmp.size() - 1, new Available(tmpI, j, tmpValue));
                        }
                    }

                    lastId = tmpI;
                }
            }

            if(!tmp.isEmpty()){

                for(Available a : tmp){

                    this.processImageRL[a.x][a.y] = a.value;
                }

                tmp.clear();
            }
        }
    }

        /**************
         * GetProcess *
         **************/
    public ImagePlus getProcess(){

        ImageProcessor tmp1 = this.baseImage.duplicate().getProcessor();
        tmp1.setFloatArray(this.processImageLR);
        ImagePlus img1 = new ImagePlus("tmp1", tmp1);

        ImageProcessor tmp2 = this.baseImage.duplicate().getProcessor();
        tmp2.setFloatArray(this.processImageRL);
        ImagePlus img2 = new ImagePlus("tmp2", tmp2);

        return new ImageOperators().applyOperator(ImageOperators.Operator.Average, img2, img1);
    }

    public ImagePlus getProcessLR(){

        ImageProcessor tmp = this.baseImage.getProcessor().duplicate();
        tmp.setFloatArray(this.processImageLR);

        return new ImagePlus("processedLR", tmp);
    }

    public ImagePlus getProcessRL(){

        ImageProcessor tmp = this.baseImage.getProcessor().duplicate();
        tmp.setFloatArray(this.processImageRL);

        return new ImagePlus("processedRL", tmp);
    }
}
