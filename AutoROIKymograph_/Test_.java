
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;

public class Test_ implements PlugIn{

    protected ImagePlus image = null;

    private RoiManager roiM;
    private int minValue = (int) Prefs.get("AutoROI_minVal.double", 0);       // Minimal value of the pixel
    private int maxValue = (int) Prefs.get("AutoROI_maxVal.double", 255);;       // Maximal value of the pixel

    @Override
    public void run(String s){

        if(WindowManager.getImageCount() != 0){

            this.image = WindowManager.getCurrentImage();
        }

        if(IJ.versionLessThan("1.52i")){

            return;
        }

        GenericDialog dialog = new GenericDialog("AutoKymograph");
        dialog.addNumericField("minValue of a pixel", this.minValue, 0);
        dialog.addNumericField("maxValue of a pixel", this.maxValue, 0);

        dialog.showDialog();

        if(dialog.wasCanceled()){

            return;
        }

        this.minValue = (int) dialog.getNextNumber();
        this.maxValue = (int) dialog.getNextNumber();

        if(this.maxValue <= this.minValue){

            IJ.error("Test_OutOfRange", "minValue and maxValue should be strictly less and greater than each other respectively");
        }

        Prefs.set("AutoROI_minVal.double", this.minValue);
        Prefs.set("AutoROI_maxVal.double", this.maxValue);


    }
}
