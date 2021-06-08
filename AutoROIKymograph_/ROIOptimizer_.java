import Required.*;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class ROIOptimizer_ implements PlugInFilter{

        /************
         * Variable *
         ************/
    protected ImagePlus image;

        /*******
         * Run *
         *******/
    @Override
    public void run(ImageProcessor imageProcessor){

        if(WindowManager.getImageCount() != 0){

            this.image = WindowManager.getCurrentImage();
        }

        ImageInfo imageInfo = new ImageInfo(image);

        GenericDialog dialog = new GenericDialog("AutoROI");

        dialog.addMessage("Please select the intensity range to be processed:");
        dialog.addSlider("Min value", 0.0, 255.0, 15.0, 0.1);
        dialog.addSlider("Max value", 0.0, 255.0, 255.0, 0.1);

        dialog.addMessage("Please set the maximal speed of a particle");
        dialog.addSlider("Minimal length between two found points", 2.0, imageInfo.width, 2.0, 1.0);

        dialog.addMessage("Image to be process");
        dialog.addImage(this.image);

        dialog.showDialog();

        if(dialog.wasCanceled()){

            return;
        }

        double minValue = dialog.getNextNumber();               // Minimal intensity value of the pixel
        double maxValue = dialog.getNextNumber();               // Maximal intensity value of the pixel
        int subSearchLength = (int) dialog.getNextNumber();     // Minimal length (in px) between two higher pixel value

        if(dialog.wasOKed()){   // Running process

            if(maxValue <= minValue){     // Principe of an interval

                IJ.error("Test_OutOfRange", "minValue and maxValue should be strictly less and greater than each other respectively");
            }

            ROIOptimizer test = new ROIOptimizer(this.image.duplicate(), minValue, maxValue, subSearchLength, imageInfo);
            WindowManager.setCurrentWindow(new ImageWindow(test.getProcess()));
            WindowManager.setCurrentWindow(new ImageWindow(test.getProcessRL()));
            WindowManager.setCurrentWindow(new ImageWindow(test.getProcessLR()));
        }
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