import Required.PresetKymograph;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.frame.PlugInFrame;
import static ij.plugin.filter.PlugInFilter.DOES_8C;

import javax.swing.*;

public class PresetKymograph_ extends PlugInFrame{

        /*************
         * Variables *
         *************/
    protected ImagePlus imageRef;
    private JButton buttonAdd;
    private JButton buttonQuit;
    private boolean OK;

        /***************
         * Constructor *
         ***************/
    public PresetKymograph_(){

        super("PresetKymograph");

        this.OK = true;

        if(IJ.versionLessThan("1.52i")){

            IJ.error("PresetKymograph_", "Minimal version allowed is 1.52i");
            this.OK = false;

            return;
        }

        if(WindowManager.getCurrentImage() == null){

            IJ.error("PresetKymograph_","You should have at least one image open");
            this.OK = false;

            return;
        }

        if(WindowManager.getCurrentImage().getType() != DOES_8C){

            IJ.error("PresetKymograph_", "The image should be have a Grey 32bits preset");
            this.OK = false;

            return;
        }

        this.imageRef = WindowManager.getCurrentImage();
        this.buttonAdd = new JButton("Add operation");
        this.buttonAdd.setSize(30, 15);
        this.buttonQuit = new JButton("Quit");
        this.buttonQuit.setSize(30, 15);

        loadPanels();
        this.run("");
    }

        /**************
         * LoadPanels *
         **************/
    private void loadPanels(){

        this.buttonAdd.addActionListener(action -> {

            if(this.buttonAdd.isEnabled()){

                this.imageRef = WindowManager.getCurrentImage();
                new PresetKymograph().run(this.imageRef.getProcessor());

            }
        });

        this.buttonQuit.addActionListener(action -> {

            if(this.buttonQuit.isEnabled()){

                this.dispose();
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Remember to save (Ctrl+S after selecting the image window) each intermediate image if you wish to use them as an operator."));
        panel.add(new JLabel("Note this plugin doesn't replace the module Image>Adjust>Brightness/Contrast... (Ctrl+MAJ+C),  you can using both of them."));
        JPanel tmp = new JPanel();
        tmp.add(this.buttonAdd);
        tmp.add(this.buttonQuit);
        panel.add(tmp);
        this.add(panel);
    }

        /*******
         * Run *
         *******/
    public void run(String arg){

        if(this.OK){

            this.setSize(930, 120);
            this.show();
        }
        else{

            this.dispose();
        }
    }
}
