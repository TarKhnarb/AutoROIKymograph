import Required.*;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class ROIOptimizer_ implements PlugInFilter{

        /*************
         * Variables *
         *************/
    protected ImagePlus image;
    private ImageInfo imageInfo;
    private double minValue = Prefs.get("ROIOpt_minVal.double", 50.0);                 // Minimal intensity value of the pixel
    private double maxValue = Prefs.get("ROIOpt_maxVal.double", 150.0);                // Maximal intensity value of the pixel
    private int subSearchLength = (int) Prefs.get("ROIOpt_subSearchLgt.double", 2.0);  // Minimal length (in px) between two higher pixel value

        /*******
         * Run *
         *******/
    @Override
    public void run(ImageProcessor imageProcessor){

        if(WindowManager.getImageCount() != 0){

            this.image = WindowManager.getCurrentImage();
        }

        this.imageInfo = new ImageInfo(image);

        GenericDialog dialog = new GenericDialog("AutoROI");

        dialog.addMessage("Please select the intensity range to be processed:");
        dialog.addSlider("Min value", 0.0, 255.0, 50.0, 0.1);
        dialog.addSlider("Max value", 0.0, 255.0, 150.0, 0.1);

        dialog.addMessage("Please set the maximal speed of a particle");
        dialog.addSlider("Minimal length between two found points", 2.0, this.imageInfo.width, 2.0, 1.0);

        dialog.addMessage("Image to be process");
        dialog.addImage(this.image);

        dialog.showDialog();

        if(dialog.wasCanceled()){

            return;
        }

        this.minValue = dialog.getNextNumber();
        this.maxValue = dialog.getNextNumber();
        this.subSearchLength = (int) dialog.getNextNumber();

            // Save preset
        Prefs.set("ROIOpt_minVal.double", this.minValue);
        Prefs.set("ROIOpt_maxVal.double", this.maxValue);
        Prefs.set("ROIOpt_subSearchLgt.double", this.subSearchLength);

        if(dialog.wasOKed()){   // Running process

            if(this.maxValue <= this.minValue){     // Principe of an interval

                IJ.error("Test_OutOfRange", "minValue and maxValue should be strictly less and greater than each other respectively");
            }

            PreShredder test = new PreShredder(this.image, this.minValue, this.maxValue, this.subSearchLength, this.imageInfo);
            WindowManager.setCurrentWindow(new ImageWindow(test.getProcess()));
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