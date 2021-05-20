package Required;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ImageShredder extends Thread{

        /*************
         * Variables *
         *************/
    private final ImagePlus image;                      // Image to process
    private final Range<Double> intensity;              // Intensity range of valid pixels
    private final int searchLength;                     // Length (in px) of search on the next line of pixel
    private final int subSearchLgth;                    // sub length (in px) for detecting group
    private final ImageInfo imageInfo;                  // Image information
    private final RoiManager roiManager;                // Window ROI
    private final float[] pixels;                       // Image pixel array
    private final ArrayList<Point> startPoints;         // Preselected points
    private ConcurrentLinkedQueue<Point> toBeProcess;   // Points that need processing
    private ArrayList<Graph<Point>> finalPaths;         // All detected points for drawing final paths

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
        this.toBeProcess = new ConcurrentLinkedQueue<>();
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

                this.startPoints.add(new Point(points.getPolygon().xpoints[0], points.getPolygon().ypoints[0], null));
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
        for(Point startPoint : this.startPoints){

            Graph<Point> tmp = new Graph<>(startPoint);
            this.finalPaths.add(tmp);
        }

        for(int k = 0; k < this.finalPaths.size(); ++k){

            processStartPoint(k);
        }
    }

        /*****************
         * GetFinalPaths *
         *****************/
    public ArrayList<Graph<Point>> getFinalPaths(){

        return this.finalPaths;
    }

        /*********************
         * ProcessStartPoint *
         *********************/
    private void processStartPoint(int index){

        this.toBeProcess.add(new Point(this.finalPaths.get(index).getRoot().getValue().x, this.finalPaths.get(index).getRoot().getValue().y, this.finalPaths.get(index).getRoot()));

        Point toProcess;
        while((toProcess = this.toBeProcess.poll()) != null){

            processLine(toProcess);
        }
    }

        /***************
         * ProcessLine *
         ***************/
    private void processLine(Point point){

        int tmpLgth = (this.searchLength + (this.searchLength%2))/2;
        int minX = (Math.max((point.x - tmpLgth), 0));
        int maxX = (Math.min((point.x + tmpLgth), this.imageInfo.width - 1));

        processSubLine(point.current, minX, maxX, point.y + 1);

        if(point.current.hasChildren() && (point.y < this.imageInfo.height - 1)){

            for(Node<Point> child : point.current.getChildren()){

                this.toBeProcess.offer(new Point(child.getValue().x, child.getValue().y, child));
            }
        }
    }
        /******************
         * ProcessSubLine *
         ******************/
    private void processSubLine(Node<Point> node, int minX, int maxX, int y){

        ArrayList<Point> tmp = new ArrayList<>();
        for(int k = minX; k < maxX; ++k){

            double value = this.pixels[k + y*this.imageInfo.width];
            if(this.intensity.belongToItself(value)){

                if(tmp.isEmpty()){

                    Point tmpP = new Point(k, y, null);
                    tmp.add(tmpP);
                }
                else{

                    if((k - tmp.get(tmp.size() - 1).x) > this.subSearchLgth){

                        Point tmpP = new Point(k, y, null);
                        tmp.add(tmpP);
                    }
                    else if(this.pixels[tmp.get(tmp.size() - 1).x + y*this.imageInfo.width] < value){

                        Point tmpP = new Point(k, y, null);
                        tmp.set(tmp.size() - 1, tmpP);
                    }
                }
            }
        }

        if(!tmp.isEmpty()){

            for(Point p : tmp){

                Node<Point> n = new Node<>(p);
                n.getValue().setCurrent(n);
                node.addchild(n);
            }
        }
    }
}
