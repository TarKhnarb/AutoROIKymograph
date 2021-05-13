
import Utilities.ImageInfo;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

public class Test_ implements PlugInFilter{

    protected ImagePlus image = null;

    private ImageInfo imageInfo = null;
    private RoiManager roiM;
    private int length = (int) Prefs.get("AutoROI_length.double", 10.0);      // Length in micrometer of the image
    private int minValue = (int) Prefs.get("AutoROI_minVal.double", 50.0);    // Minimal intensity value of the pixel
    private int maxValue = (int) Prefs.get("AutoROI_maxVal.double", 150.0);   // Maximal intensity value of the pixel
    private int maxSpeed = (int) Prefs.get("AutoROI_maxSpd.double", 30.0);    // Maximal intensity value of the pixel

    @Override
    public void run(ImageProcessor imageProcessor){

        if(WindowManager.getImageCount() != 0){

            this.image = WindowManager.getCurrentImage();
        }
        imageInfo = new ImageInfo(image);

        GenericDialog dialog = new GenericDialog("AutoROI");


        dialog.addMessage("Please select the length in micrometer of the image:");
        dialog.addNumericField("length (ym)", length);

        dialog.addMessage("Please select the intensity range to be processed:");
        dialog.addSlider("minValue", 0.0, 255.0, 50.0, 1.0);
        dialog.addSlider("maxValue", 0.0, 255.0, 150.0, 1.0);

        dialog.addMessage("Please set the maximal speed of a particle");
        dialog.addSlider("maxSpeed (px)", 1.0, imageInfo.width, 30, 1.0);

        dialog.addMessage("Image to be process");
        dialog.addImage(image);

        dialog.addMessage("Image information:");
        dialog.addMessage("");
        dialog.addMessage("     Duration: " + imageInfo.duration);
        dialog.addMessage("     Number of frames: " + imageInfo.nbFrames);
        dialog.addMessage("     Image width (px): " + imageInfo.width);
        dialog.addMessage("     Image height (px): " + imageInfo.height);
        dialog.addMessage("     Image Type (int): " + imageInfo.type);


        dialog.showDialog();

        if(dialog.wasCanceled()){

            return;
        }

        this.length = (int) dialog.getNextNumber();
        this.minValue = (int) dialog.getNextNumber();
        this.maxValue = (int) dialog.getNextNumber();
        this.maxSpeed = (int) dialog.getNextNumber();

        if(this.maxValue <= this.minValue){

            IJ.error("Test_OutOfRange", "minValue and maxValue should be strictly less and greater than each other respectively");
        }

            //sauvegarde les précédents params
        Prefs.set("AutoROI_length.double", this.length);
        Prefs.set("AutoROI_minVal.double", this.minValue);
        Prefs.set("AutoROI_maxVal.double", this.maxValue);
        Prefs.set("AutoROI_maxSpd.double", this.maxSpeed);


    }

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
