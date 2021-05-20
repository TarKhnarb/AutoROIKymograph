package Required;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.plugin.PlugIn;

import javax.swing.*;
import java.io.File;

public class PresetImage implements PlugIn{

        /*************
         * Variables *
         *************/
    private ImagePlus image;
    private static File dir;

        /*******
         * Run *
         *******/
    @Override
    public void run(String s){

        this.image = WindowManager.getCurrentImage();
        String[] values = {"Variable", "Image"};
        GenericDialog dialog = new GenericDialog("Operator");
        dialog.addRadioButtonGroup("Use operator with a/an: ", values, 2, 2, values[0]);

        dialog.showDialog();

        if(dialog.wasOKed()){

            String result = dialog.getNextRadioButton();
            if(result.equals(values[0])){

                GenericDialog operator1 = new GenericDialog("Set operator " + result);
                String[] opName = {"Reverse", "Add", "Subtract", "Multiply", "Divide", "Min", "Max", "AND", "OR", "XOR", "Gamma", "Log2", "Logarithm", "Exponential", "Square", "SquareRoot","Absolute", "Average", "None"};
                operator1.addNumericField("Value", 1.0, 1);
                operator1.addRadioButtonGroup("Please select the operator to be process", opName, opName.length, 1, opName[18]);

                operator1.showDialog();

                if(operator1.wasOKed()){

                    ImageOperators op = new ImageOperators();
                    this.image = op.applyOperator(ImageOperators.Operator.valueOf(operator1.getNextRadioButton()), this.image, operator1.getNextNumber());
                    WindowManager.setTempCurrentImage(this.image);
                }
            }
            else if(result.equals(values[1])){

                ImagePlus toProcess = null;
                JFileChooser fc;
                try{

                    fc = new JFileChooser();
                }
                catch(Throwable e){

                    IJ.error("This plugin requires Java 2 or Swing.");
                    return;
                }

                fc.setMultiSelectionEnabled(true);
                if(dir == null){

                    String subDir = OpenDialog.getDefaultDirectory();
                    if(subDir != null){

                        dir = new File(subDir);
                    }
                }

                if(dir != null){

                    fc.setCurrentDirectory(dir);
                }

                int returnVal = fc.showOpenDialog(IJ.getInstance());
                if(returnVal != JFileChooser.APPROVE_OPTION){

                    return;
                }

                File[] files = fc.getSelectedFiles();
                if(files.length == 0){ // getSelectedFiles does not work on some JVMs

                    files = new File[1];
                    files[0] = fc.getSelectedFile();
                }

                String path = fc.getCurrentDirectory().getPath() + Prefs.getFileSeparator();
                dir = fc.getCurrentDirectory();
                Opener opener = new Opener();
                for (File file : files) {

                    ImagePlus img = opener.openImage(path, file.getName());
                    if ((img != null) && (img.getType() == 2)) {

                        toProcess = img;
                    }
                }

                if(toProcess == null){

                    IJ.error("The selected image isn't processable");
                }

                GenericDialog operator = new GenericDialog("Set operator " + result);
                String[] opName = {"Add", "Subtract", "Multiply", "Divide", "Min", "Max", "Average", "Difference", "None"};

                operator.addImage(toProcess);
                operator.addRadioButtonGroup("Please select the operator to be process", opName, opName.length, 1, opName[8]);

                operator.showDialog();
                if(operator.wasOKed()){

                    ImageOperators op = new ImageOperators();
                    this.image = op.applyOperator(ImageOperators.Operator.valueOf(operator.getNextRadioButton()), this.image, toProcess);
                    WindowManager.setTempCurrentImage(this.image);
                }
            }
        }
    }

        /************
         * GetImage *
         ************/
    public ImagePlus getImage(){

        return this.image;
    }
}
