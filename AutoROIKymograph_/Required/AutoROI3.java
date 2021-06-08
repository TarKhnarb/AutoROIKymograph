package Required;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.process.FloatPolygon;

import java.util.ArrayList;
import java.util.LinkedList;

public class AutoROI3{

        /**************
         * Coordinate *
         **************/
    public static class Coordinate{

            /*************
             * Variables *
             *************/
        public final int x;
        public final int y;

            /***************
             * Constructor *
             ***************/
        public Coordinate(int x, int y){

            this.x = x;
            this.y = y;
        }
    }

        /*************
         * Variables *
         *************/
    private final Range<Double> intensity;                      // Intensity range of valid pixels
    private final int lineWidth;
    private final ImageInfo imageInfo;                          // Image information
    private final RoiManager roiManager;                        // Window ROI
    private final float[] pixels;                               // Image pixel array
    private final ArrayList<FloatPolygon> startLines;                // Preselected points
    private final LinkedList<LinkedList<Coordinate>> finalPaths;                // All detected points for drawing final paths

        /***************
         * Constructor *
         ***************/
    public AutoROI3(ImagePlus image, double minIntensity, double maxIntensity, int lineWidth, ImageInfo imageInfo, RoiManager roiManager){

            // Image to process
        this.intensity = new Range<>(minIntensity, maxIntensity);
        this.lineWidth = (lineWidth + lineWidth%2)/2;
        this.imageInfo = imageInfo;
        this.roiManager = roiManager;
        this.pixels = (float[]) image.getProcessor().getPixels();
        this.startLines = new ArrayList<>();
        this.finalPaths = new LinkedList<>();

        setStartingPoints();
        processImage();
    }

        /*********************
         * SetStartingPoints *
         *********************/
    private void setStartingPoints(){

        Roi[] tmpRoi = this.roiManager.getRoisAsArray();
        for(Roi points : tmpRoi){

            if((points.getType() == Roi.FREELINE) && (points.getPolygon().npoints > 1)){

                this.startLines.add(points.getInterpolatedPolygon(1.0, false));
            }
        }

        if(this.startLines.size() == 0){

            IJ.error("No starting point detected please make the selection and restart the plugin ");
        }
    }

        /****************
         * ProcessImage *
         ****************/
    private void processImage(){

        for(FloatPolygon roi : this.startLines){

            LinkedList<Coordinate> tmp = new LinkedList<>();
            tmp.offer(new Coordinate((int) roi.xpoints[0], (int) roi.ypoints[0]));
            this.finalPaths.add(tmp);

            processStartPoint(roi);
        }
    }

        /*****************
         * GetFinalPaths *
         *****************/
    public LinkedList<LinkedList<Coordinate>> getFinalPaths(){

        return this.finalPaths;
    }

        /*********************
         * ProcessStartPoint *
         *********************/
    private void processStartPoint(FloatPolygon roi){

        float[] X = roi.xpoints;
        float[] Y = roi.ypoints;

        for(int i = 1; i < roi.npoints; ++i){

            if(((int) Y[i] > this.finalPaths.getLast().getLast().y) && ((int) Y[i] < this.imageInfo.height)){

                Coordinate tmp = processLine(Math.max(((int) X[i]) - this.lineWidth, 0), Math.min(((int) X[i]) + this.lineWidth, this.imageInfo.width - 1), (int) Y[i]);
                if(tmp != null){

                    this.finalPaths.getLast().offer(tmp);
                }
            }
        }
    }

        /***************
         * ProcessLine *
         ***************/
    private Coordinate processLine(int minX, int maxX, int y){

        Coordinate toReturn = null;
        float oldValue = -1.0f;
        while(minX != maxX){

            float value = this.pixels[minX + y*this.imageInfo.width];
            if(this.intensity.belongToItself(value)){

                if(toReturn == null){

                    toReturn = new Coordinate(minX, y);
                }
                else{

                    if(oldValue < value){

                        toReturn = new Coordinate(minX, y);
                    }
                }

                oldValue = value;
            }

            ++minX;
        }

        return toReturn;
    }
}
