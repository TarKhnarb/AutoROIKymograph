package Required;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class PresetKymograph implements PlugInFilter{

        /*********
         * SetUp *
         *********/
    @Override
    public int setup(String s, ImagePlus imagePlus) {

        if(IJ.versionLessThan("1.52i")){

            IJ.error("PresetKymograph_", "Minimal version allowed is 1.52i");
            return DONE;
        }

        if(WindowManager.getImageCount() == 0){

            IJ.error("PresetKymograph_","You should have at least one image open");
            return DONE;
        }

        if(WindowManager.getCurrentImage().getType() != DOES_8C){

            IJ.error("PresetKymograph_", "The image should be have a Grey 32bits preset");
            return DONE;
        }

        return DOES_8C;
    }

        /*******
         * Run *
         *******/
    @Override
    public void run(ImageProcessor imageProcessor) {

        GenericDialog dialog = new GenericDialog("PresetImage");
        dialog.addImage(new ImagePlus("test", imageProcessor));
        dialog.addMessage("Image name: " + WindowManager.getCurrentImage().getTitle());

        dialog.showDialog();
        if(dialog.wasOKed()){

            PresetImage preset = new PresetImage();
            preset.run("Tests");
            WindowManager.setCurrentWindow(new ImageWindow(preset.getImage()));
        }
    }
}
