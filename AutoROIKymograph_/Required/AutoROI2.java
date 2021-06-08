package Required;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AutoROI2{

        /*************
         * Variables *
         *************/
    private final Range<Double> intensity;                      // Intensity range of valid pixels
    private final int searchLgth;                               // sub length (in px) for detecting group
    private final int subSearchLgth;                            // sub length (in px) for detecting group
    private final ImageInfo imageInfo;                          // Image information
    private final RoiManager roiManager;                        // Window ROI
    private final float[] pixels;                               // Image pixel array
    private final ArrayList<Vector> startPoints;                // Preselected points
    private final ConcurrentLinkedQueue<Vector> toBeProcess;    // Points that need processing
    private ArrayList<Graph<Vector>> finalPaths;                // All detected points for drawing final paths

        /***************
         * Constructor *
         ***************/
    public AutoROI2(ImagePlus image, double minIntensity, double maxIntensity, int searchLgth, int subSearchLgth, ImageInfo imageInfo, RoiManager roiManager){

            // Image to process
        this.intensity = new Range<>(minIntensity, maxIntensity);
        this.searchLgth = searchLgth;
        this.subSearchLgth = subSearchLgth;
        this.imageInfo = imageInfo;
        this.roiManager = roiManager;
        this.pixels = (float[]) image.getProcessor().getPixels();
        this.toBeProcess = new ConcurrentLinkedQueue<>();
        this.startPoints = new ArrayList<>();

        getStartingPoints();
        processImage();
    }

        /*********************
         * GetStartingPoints *
         *********************/
    private void getStartingPoints(){

        Roi[] tmpRoi = this.roiManager.getRoisAsArray();
        for(Roi points : tmpRoi){

            if((points.getType() == Roi.POLYLINE) && (points.getPolygon().npoints > 1)){

                this.startPoints.add(new Vector(
                        new Coord(points.getPolygon().xpoints[0],
                                  points.getPolygon().ypoints[0]),
                        new Coord(points.getPolygon().xpoints[1],
                                  points.getPolygon().ypoints[1]),
                        this.pixels[points.getPolygon().xpoints[0] + points.getPolygon().ypoints[0]*this.imageInfo.width],
                        this.pixels[points.getPolygon().xpoints[1] + points.getPolygon().ypoints[1]*this.imageInfo.width],
                        null));
            }
        }

        if(this.startPoints.size() == 0){

            IJ.error("No starting point detected please make the selection and restart the plugin ");
        }
    }

        /****************
         * ProcessImage *
         ****************/
    private void processImage(){

        this.finalPaths = new ArrayList<>();
        for(Vector startPoint : this.startPoints){

            this.finalPaths.add(new Graph<>(startPoint));
            this.finalPaths.get(this.finalPaths.size() - 1).getRoot().getValue().setItself(this.finalPaths.get(finalPaths.size() - 1).getRoot());
        }

        for(int k = 0; k < this.finalPaths.size(); ++k){

            processStartPoint(k);
        }
    }

        /*****************
         * GetFinalPaths *
         *****************/
    public ArrayList<Graph<Vector>> getFinalPaths(){

        return this.finalPaths;
    }

        /*********************
         * ProcessStartPoint *
         *********************/
    private void processStartPoint(int index){

        this.toBeProcess.add(this.finalPaths.get(index).getRoot().getValue());

        Vector toProcess;
        while((toProcess = this.toBeProcess.poll()) != null){

            processLine(toProcess);
        }
/*
        int useTh = 1;
        Vector toProcess;
        while(((toProcess = this.toBeProcess.poll()) != null)){


        }*/
        /*
        AtomicInteger usedTh = new AtomicInteger();
        for(int k = 0; k < Prefs.getThreads(); ++k){

            usedTh.incrementAndGet();
            new Thread(() -> {

                while(!this.toBeProcess.isEmpty() || usedTh.get() != 0){

                    Vector toProcess = this.toBeProcess.poll();
                    processLine(toProcess);
                }

                usedTh.decrementAndGet();
            });
        }*/
    }

        /***************
         * ProcessLine *
         ***************/
    private void processLine(Vector vect){

        if(vect.v.x >= 0){

            processSubLine(vect.itself, (Math.max((vect.b.x - this.searchLgth), 0)), (Math.min((vect.b.x + this.searchLgth), this.imageInfo.width - 1)), vect.b.y + 1);
        }
        else{

            processSubLine(vect.itself, (Math.min((vect.b.x + this.searchLgth), this.imageInfo.width - 1)), (Math.max((vect.b.x - this.searchLgth), 0)), vect.b.y + 1);
        }
    }
        /******************
         * ProcessSubLine *
         ******************/
    private void processSubLine(Node<Vector> node, int minX, int maxX, int y){

        Range<Double> tmpIntensity = new Range<>(node.getValue().aver*0.4, node.getValue().aver*1.6);
        ArrayList<Vector> potentialVector = new ArrayList<>();

        int k = minX;
        int j = (minX <= maxX ? 1 : -1);
        while(k != (maxX - j)){

            float value = this.pixels[k + y*this.imageInfo.width];
            if(this.intensity.belongToItself(value)){

                if(potentialVector.isEmpty() && tmpIntensity.belongToItself(value)){

                    potentialVector.add(new Vector(node.getValue().b, new Coord(k, y), this.pixels[node.getValue().b.x + node.getValue().b.y * this.imageInfo.width], value, null));
                }
                else if(!potentialVector.isEmpty() && (Math.abs(k - potentialVector.get(potentialVector.size() - 1).b.x) >= this.subSearchLgth) && (tmpIntensity.belongToItself(value))){

                    potentialVector.add(new Vector(node.getValue().b, new Coord(k, y), this.pixels[node.getValue().b.x + node.getValue().b.y * this.imageInfo.width], value, null));
                }
            }

            k += j;
        }
        /*
        for(int k = minX; k != (maxX - j); k = k + j){

            float value = this.pixels[k + y*this.imageInfo.width];
            if(this.intensity.belongToItself(value)){

                if(potentialVector.isEmpty() && tmpIntensity.belongToItself(value)){

                    potentialVector.add(new Vector(node.getValue().b, new Coord(k, y), this.pixels[node.getValue().b.x + node.getValue().b.y * this.imageInfo.width], value, null));
                }
                else if(Math.abs(k - potentialVector.get(potentialVector.size() - 1).b.x) >= this.subSearchLgth && (tmpIntensity.belongToItself(value))){

                    potentialVector.add(new Vector(node.getValue().b, new Coord(k, y), this.pixels[node.getValue().b.x + node.getValue().b.y * this.imageInfo.width], value, null));
                }
            }
        }*/

        if(!potentialVector.isEmpty()){

            for(Vector p : potentialVector){

                Node<Vector> n = new Node<>(p);
                n.getValue().setItself(n);
                node.addchild(n);
                if(p.b.y < this.imageInfo.height - 1){

                    this.toBeProcess.add(p);
                }
            }
        }
        else{

            if((y + 1) <= this.imageInfo.height - 1){

                processSubLine(node, minX, maxX, y + 1);
            }
        }
    }
}
