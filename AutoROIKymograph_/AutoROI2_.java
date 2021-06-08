import Required.*;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.util.ArrayList;

public class AutoROI2_ implements PlugInFilter {

        /*************
         * Variables *
         *************/
    protected ImagePlus image;
    private RoiManager roiManager;

    private ImageInfo imageInfo;
    private double minValue = Prefs.get("AutoROI_minVal.double", 15.0);                 // Minimal intensity value of the pixel
    private double maxValue = Prefs.get("AutoROI_maxVal.double", 255.0);                // Maximal intensity value of the pixel
    private int searchLength = (int) Prefs.get("AutoROI_searchLgt.double", 30.0);       // Length of search on the next line
    private int subSearchLength = (int) Prefs.get("AutoROI_subSearchLgt.double", 6.0);  // Minimal length (in px) between two higher pixel value

    /*******
     * Run *
     *******/
    @Override
    public void run(ImageProcessor imageProcessor){

        if(WindowManager.getImageCount() != 0){

            this.image = WindowManager.getCurrentImage().duplicate();
        }

        this.imageInfo = new ImageInfo(image);
        this.roiManager = RoiManager.getInstance(); // ROI manager required
        if(this.roiManager == null){

            IJ.error("AutoROI requires the RoiManager to be opened");
            return;
        }

        Roi[] tempRoi = this.roiManager.getRoisAsArray();
        int count = 0;
        for(int i = 0; i < this.roiManager.getCount(); ++i){

            if(tempRoi[i].getType() == Roi.POLYLINE){

                if(tempRoi[i].getPolygon().xpoints.length == 2){

                    if(tempRoi[i].getPolygon().ypoints[0] < tempRoi[i].getPolygon().ypoints[1]){

                        ++count;
                    }
                }
            }
        }

        if(count == 0){

            IJ.error("AutoROI2 requires at least one segmented line with at least two points");
            return;
        }

        GenericDialog dialog = new GenericDialog("AutoROI");

        dialog.addMessage("Please select the intensity range to be processed:");
        dialog.addSlider("Min value", 0.0, 255.0, this.minValue, 0.1);
        dialog.addSlider("Max value", 0.0, 255.0, this.maxValue, 0.1);

        dialog.addMessage("Please set the maximal speed of a particle");
        dialog.addSlider("Search length (px)", 1.0, this.imageInfo.width, this.searchLength, 1.0);
        dialog.addSlider("Minimal length (px)", 2.0, this.imageInfo.width, this.subSearchLength, 1.0);

        dialog.addMessage("Image to be process");
        dialog.addImage(this.image);

        dialog.addMessage("Image information:");
        dialog.addMessage("     Number of frames: " + this.imageInfo.nbFrames);
        dialog.addMessage("     Image width (px): " + this.imageInfo.width);
        dialog.addMessage("     Image height (px): " + this.imageInfo.height);

        dialog.showDialog();

        if(dialog.wasCanceled()){

            return;
        }

        // Values affectation
        this.minValue = dialog.getNextNumber();
        this.maxValue = dialog.getNextNumber();
        this.searchLength = (int) dialog.getNextNumber();
        this.subSearchLength = (int) dialog.getNextNumber();

        // Save preset
        Prefs.set("AutoROI_minVal.double", this.minValue);
        Prefs.set("AutoROI_maxVal.double", this.maxValue);
        Prefs.set("AutoROI_searchLgt.double", this.searchLength);
        Prefs.set("AutoROI_subSearchLgt.double", this.subSearchLength);

        if(dialog.wasOKed()){   // Running process

            if(this.maxValue <= this.minValue){     // Principe of an interval

                IJ.error("Test_OutOfRange", "minValue and maxValue should be strictly less and greater than each other respectively");
            }

            AutoROI2 autoROI = new AutoROI2(this.image, this.minValue, this.maxValue, this.searchLength, this.subSearchLength, this.imageInfo, this.roiManager);
            System.out.println("Initialised");
            processResults(autoROI.getFinalPaths());
            System.out.println("End");
        }
    }

    /******************
     * ProcessResults *
     ******************/
    private void processResults(ArrayList<Graph<Vector>> graphs){

        this.roiManager.reset();
        for(Graph<Vector> graph : graphs){

            for(ArrayList<Node<Vector>> path : graph.getAllPaths()){

                this.roiManager.addRoi(new PolygonRoi(getXCoordinates(path), getYCoordinates(path), path.size() + 1, Roi.POLYLINE));
            }
        }
    }

    /*******************
     * GetXCoordinates *
     *******************/
    private int[] getXCoordinates(ArrayList<Node<Vector>> path){

        int[] toReturn = new int[path.size() + 1];
        toReturn[0] = path.get(0).getValue().a.x;
        for(int i = 0; i < path.size(); ++i){

            toReturn[i + 1] = path.get(i).getValue().b.x;
        }

        return toReturn;
    }

    /*******************
     * GetYCoordinates *
     *******************/
    private int[] getYCoordinates(ArrayList<Node<Vector>> path){


        int[] toReturn = new int[path.size() + 1];
        toReturn[0] = path.get(0).getValue().a.y;
        for(int i = 0; i < path.size(); ++i){

            toReturn[i + 1] = path.get(i).getValue().b.y;
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
