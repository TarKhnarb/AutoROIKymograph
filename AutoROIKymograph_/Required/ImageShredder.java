package Required;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.awt.Polygon;
import java.util.ArrayList;

public class ImageShredder {

    /*************
     * Variables *
     *************/
    private final ImagePlus image;                  // Image to process
    private final Range<Double> intensity;          // Intensity range of valid pixels
    private final int searchLength;                 // Length (in px) of search on the next line of pixel
    private final int subSearchLgth;                // sub length (in px) for detecting group
    private final ImageInfo imageInfo;              // Image information
    private final RoiManager roiManager;            // Window ROI
    private final float[] pixels;                   // Image pixel array
    private final ArrayList<Polygon> startPoints;   // Preselected points

    private ArrayList<Graph<Polygon>> finalPaths;   // All detected points for drawing final paths

        /***************
         * Constructor *
         ***************/
    public ImageShredder(ImagePlus image, double minIntensity, double maxIntensity, int searchLength, int subSearchLgth, ImageInfo imageInfo, RoiManager roiManager){

        this.image = image;
        this.intensity = new Range<>(minIntensity, maxIntensity);
        this.searchLength = searchLength;
        this.subSearchLgth = subSearchLgth;
        this.imageInfo = imageInfo;
        this.roiManager = roiManager;
        this.pixels = (float[]) this.image.getProcessor().getPixels();

        this.startPoints = new ArrayList<>();
        getStartingPoints();
    }

        /*********************
         * GetStartingPoints *
         *********************/
    private void getStartingPoints(){

        Roi[] tmpRoi = this.roiManager.getRoisAsArray();
        for(Roi points : tmpRoi){

            if((points.getType() == Roi.POINT) && (points.getPolygon().npoints != 0)){

                this.startPoints.add(points.getPolygon());
            }
        }

        if(this.startPoints.size() == 0){

            IJ.error("No starting point detected please make the selection and restart the plugin ");
        }
    }

        /****************
         * ProcessImage *
         ****************/
    public void processImage(){

        System.out.println("Start image process");
        this.finalPaths = new ArrayList<>();
        for (Polygon startPoint : this.startPoints) {

            System.out.println("Start pt pixel value: " + pixels[startPoint.xpoints[0] + startPoint.ypoints[0] * this.imageInfo.width]);
            System.out.println("Range: " + intensity.getMin() + " / " + intensity.getMax());
            System.out.println("Lengths: " + searchLength + " / " + subSearchLgth);
            System.out.println();
            Graph<Polygon> tmp = new Graph<>(startPoint);
            this.finalPaths.add(tmp);
        }

        for(int k = 0; k < this.finalPaths.size(); ++k){

            processStartPoint(k);
        }
    }

        /*****************
         * GetFinalPaths *
         *****************/
    public ArrayList<Graph<Polygon>> getFinalPaths(){

        return this.finalPaths;
    }

        /*********************
         * ProcessStartPoint *
         *********************/
    private void processStartPoint(int index){

        int startX = this.finalPaths.get(index).getRoot().getValue().xpoints[0];
        int startY = this.finalPaths.get(index).getRoot().getValue().ypoints[0];

        processLine(this.finalPaths.get(index).getRoot(), startX, ++startY);
    }

        /***************
         * ProcessLine *
         ***************/
    private void processLine(Node<Polygon> currentNode, int x, int y){

        System.out.println("Process line n°" + y);
        int tmpLgth = (this.searchLength + (this.searchLength%2))/2;
        int minX = (Math.max((x - tmpLgth), 0));
        int maxX = (Math.min((x + tmpLgth), this.imageInfo.width - 1));

        for(int k = minX; k < maxX; k += subSearchLgth){

            processSubLine(currentNode, k, y);
        }

        if(currentNode.hasChildren() && (y < imageInfo.height - 2)){

            for(Node<Polygon> child : currentNode.getChildren()){

                processLine(child, child.getValue().xpoints[0], ++y);
            }
        }
    }

        /******************
         * ProcessSubLine *
         ******************/
    private void processSubLine(Node<Polygon> node, int x, int y){

        int tmp = -1;
        for(int k = x; k < x + this.subSearchLgth; ++k){

            System.out.print("          Pixel process: " + k + " / " + y);
            double value = this.pixels[k + y*this.imageInfo.width];
            if(this.intensity.belongToItself(value)){

                System.out.print(" OUI: " + value + "\n");
                if(tmp == -1){

                    tmp = k;
                }
                else{

                    if(this.pixels[tmp + y*this.imageInfo.width] < value){

                        tmp = k;
                    }
                }
            }
            else{

                System.out.print(" NON: " + value + "\n");
            }
        }

        if(tmp != -1){

            System.out.println("      Trouvé : " + tmp + " / " + y );
            Polygon tmpPoly = new Polygon();
            tmpPoly.addPoint(tmp, y);
            node.addchild(new Node<>(tmpPoly));
        }
        else{

            System.out.println("      Rien trouvé");
        }
    }
}
