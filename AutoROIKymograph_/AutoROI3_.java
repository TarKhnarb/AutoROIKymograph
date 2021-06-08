import Required.*;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.util.LinkedList;

public class AutoROI3_ implements PlugInFilter {

        /*************
         * Variables *
         *************/
    protected ImagePlus image;
    private RoiManager roiManager;

        /*******
         * Run *
         *******/
    @Override
    public void run(ImageProcessor imageProcessor){

        if(WindowManager.getImageCount() != 0){

            this.image = WindowManager.getCurrentImage().duplicate();
        }

        ImageInfo imageInfo = new ImageInfo(image);
        this.roiManager = RoiManager.getInstance(); // ROI manager required
        if(this.roiManager == null){

            IJ.error("AutoROI requires the RoiManager to be opened");
            return;
        }

        Roi[] tempRoi = this.roiManager.getRoisAsArray();
        int count = 0;
        for(int i = 0; i < this.roiManager.getCount(); ++i){

            if(tempRoi[i].getType() == Roi.FREELINE){

                ++count;
            }
        }

        if(count == 0){

            IJ.error("AutoROI2 requires at least one freehand line");
            return;
        }

        GenericDialog dialog = new GenericDialog("AutoROI");

        dialog.addMessage("Please select the intensity range to be processed:");
        dialog.addSlider("Min value", 0.0, 255.0, 15.0, 0.1);
        dialog.addSlider("Max value", 0.0, 255.0, 255.0, 0.1);

        dialog.addMessage("Please set the following parameters");
        dialog.addSlider("Line width (px)", 1.0, imageInfo.width, 6, 1.0);

        dialog.addMessage("Image to be process");
        dialog.addImage(this.image);

        dialog.addMessage("Image information:");
        dialog.addMessage("     Number of frames: " + imageInfo.nbFrames);
        dialog.addMessage("     Image width (px): " + imageInfo.width);
        dialog.addMessage("     Image height (px): " + imageInfo.height);

        dialog.showDialog();

        if(dialog.wasCanceled()){

            return;
        }

            // Values affectation
        double minValue = dialog.getNextNumber();
        double maxValue = dialog.getNextNumber();
        int lineWidth = (int) dialog.getNextNumber();

        if(dialog.wasOKed()){   // Running process

            if(maxValue <= minValue){     // Principe of an interval

                IJ.error("Test_OutOfRange", "minValue and maxValue should be strictly less and greater than each other respectively");
            }

            AutoROI3 autoROI = new AutoROI3(this.image, minValue, maxValue, lineWidth, imageInfo, this.roiManager);
            System.out.println("Start");
            processResults(autoROI.getFinalPaths());
            System.out.println("End");
        }
    }

        /******************
         * ProcessResults *
         ******************/
    private void processResults(LinkedList<LinkedList<AutoROI3.Coordinate>> paths){

        this.roiManager.reset();
        for(LinkedList<AutoROI3.Coordinate> path : paths){

             this.roiManager.addRoi(new PolygonRoi(getXCoordinates(path), getYCoordinates(path), path.size(), Roi.POLYLINE));
        }
    }

        /*******************
         * GetXCoordinates *
         *******************/
    private int[] getXCoordinates(LinkedList<AutoROI3.Coordinate> path){

        int[] toReturn = new int[path.size()];
        for(int i = 0; i < path.size(); ++i){

            toReturn[i] = path.get(i).x;
        }

        return toReturn;
    }

        /*******************
         * GetYCoordinates *
         *******************/
    private int[] getYCoordinates(LinkedList<AutoROI3.Coordinate> path){


        int[] toReturn = new int[path.size()];
        for(int i = 0; i < path.size(); ++i){

            toReturn[i] = path.get(i).y;
        }

        return toReturn;
    }

        /*********
         * Setup *
         *********/
    @Override
    public int setup(String s, ImagePlus imagePlus){

        if(IJ.versionLessThan("1.52i")){

            return DONE;
        }
        else{

            return DOES_32;
        }
    }
}
