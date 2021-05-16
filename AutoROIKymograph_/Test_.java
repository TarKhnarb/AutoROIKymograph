import Required.Graph;
import Required.ImageInfo;
import Required.ImageShredder;
import Required.Node;
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

import java.awt.*;
import java.util.ArrayList;

public class Test_ implements PlugInFilter{

        /*************
         * Variables *
         *************/
    protected ImagePlus image = null;
    private RoiManager roiManager;

    private ImageInfo imageInfo = null;
    private double minValue = Prefs.get("AutoROI_minVal.double", 50.0);                 // Minimal intensity value of the pixel
    private double maxValue = Prefs.get("AutoROI_maxVal.double", 150.0);                // Maximal intensity value of the pixel
    private int searchLength = (int) Prefs.get("AutoROI_searchLgt.double", 30.0);       // Maximal intensity value of the pixel
    private int subSearchLength = (int) Prefs.get("AutoROI_subSearchLgt.double", 2.0);  // Maximal intensity value of the pixel

        /*******
         * Run *
         *******/
    @Override
    public void run(ImageProcessor imageProcessor){

        if(WindowManager.getImageCount() != 0){

            this.image = WindowManager.getCurrentImage();
        }

        imageInfo = new ImageInfo(image);
        this.roiManager = RoiManager.getInstance(); // ROI manager required
        if(this.roiManager == null){

            IJ.error("AutoROI requires the RoiManager to be opened");
            return;
        }

        Roi[] tempRoi = roiManager.getRoisAsArray();
        int count = 0;
        for(int i = 0; i < roiManager.getCount(); ++i){

            if(tempRoi[i].getType() == Roi.POINT){

                ++count;
            }
        }

        if(count == 0){

            IJ.error("AutoROI requires at least one point selection");
            return;
        }

        GenericDialog dialog = new GenericDialog("AutoROI");

        dialog.addMessage("Please select the intensity range to be processed:");
        dialog.addSlider("Min value", 0.0, 255.0, 50.0, 0.1);
        dialog.addSlider("Max value", 0.0, 255.0, 150.0, 0.1);

        dialog.addMessage("Please set the maximal speed of a particle");
        dialog.addSlider("Search length (px)", 1.0, imageInfo.width, 30, 1.0);
        dialog.addSlider("Sub-search length", 2.0, searchLength, 2.0, 1.0);

        dialog.addMessage("Image to be process");
        dialog.addImage(image);

        dialog.addMessage("Image information:");
        dialog.addMessage("     Number of frames: " + imageInfo.nbFrames);
        dialog.addMessage("     Image width (px): " + imageInfo.width);
        dialog.addMessage("     Image height (px): " + imageInfo.height);
        dialog.addMessage("     Image Type (int): " + imageInfo.type);


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

            if(this.searchLength < this.subSearchLength){

                IJ.error("Test_OutOfRange", "subSearchLength and searchLength should be strictly less and greater than each other respectively");
            }

            ImageShredder imageShredder = new ImageShredder(this.image, this.minValue, this.maxValue, this.searchLength, this.subSearchLength, this.imageInfo, this.roiManager);
            imageShredder.processImage();
            processResults(imageShredder.getFinalPaths());
        }
    }


        /******************
         * ProcessResults *
         ******************/
    private void processResults(ArrayList<Graph<Polygon>> graphs){

        this.roiManager.reset();
        for(Graph<Polygon> graph : graphs){

            //System.out.println("Ceci est un nouvel arbre");
            for(ArrayList<Node<Polygon>> path : graph.getAllPaths()){

                this.roiManager.addRoi(new PolygonRoi(getXCoordinates(path), getYCoordinates(path), path.size(), Roi.POLYLINE));
                /*
                for(Node<Polygon> point : path){

                    System.out.println("    x: " + point.getValue().xpoints[0] + " / y: " + point.getValue().ypoints[0]);
                }
                */
            }
        }
    }


        /*******************
         * GetXCoordinates *
         *******************/
    private int[] getXCoordinates(ArrayList<Node<Polygon>> path){

        int[] toReturn = new int[path.size()];

        for(int i = 0; i < path.size(); ++i){

            toReturn[i] = path.get(i).getValue().xpoints[0];
        }

        return toReturn;
    }

        /*******************
         * GetYCoordinates *
         *******************/
    private int[] getYCoordinates(ArrayList<Node<Polygon>> path){


        int[] toReturn = new int[path.size()];

        for(int i = 0; i < path.size(); ++i){

            toReturn[i] = path.get(i).getValue().ypoints[0];
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
