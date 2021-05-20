package Required;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ImageOperators{

        /*************************
         * Enumeration: Operator *
         *************************/
    public enum Operator{

        Reverse,
        Add,
        Subtract,
        Multiply,
        Divide,
        Min,
        Max,
        AND,
        OR,
        XOR,
        Gamma,
        Log2,
        Logarithm,
        Exponential,
        Square,
        SquareRoot,
        Absolute,
        Average,
        Difference,
        None,
        All
    }

        /*****************
         * ApplyOperator *
         *****************/
    public ImagePlus applyOperator(Operator op, ImagePlus base, double value){

        switch(op){

            case Reverse:
                return operatorRev(base.duplicate().getProcessor());

            case Add:
                return operatorAdd(base.duplicate().getProcessor(), value);

            case Subtract:
                return operatorSub(base.duplicate().getProcessor(), value);

            case Multiply:
                return operatorMult(base.duplicate().getProcessor(), value);

            case Divide:
                return operatorDiv(base.duplicate().getProcessor(), value);

            case Min:
                return operatorMin(base.duplicate().getProcessor(), value);

            case Max:
                return operatorMax(base.duplicate().getProcessor(), value);

            case AND:
                return operatorAnd(base.duplicate().getProcessor(), (int) value);

            case OR:
                return operatorOr(base.duplicate().getProcessor(), (int) value);

            case XOR:
                return operatorXor(base.duplicate().getProcessor(), (int) value);

            case Gamma:
                return operatorGamma(base.duplicate().getProcessor(), value);

            case Log2:
                return operatorLog(base.duplicate().getProcessor());

            case Logarithm:
                return operatorLn(base.duplicate().getProcessor());

            case Exponential:
                return operatorExp(base.duplicate().getProcessor());

            case Square:
                return operatorSqr(base.duplicate().getProcessor());

            case SquareRoot:
                return operatorSqrt(base.duplicate().getProcessor());

            case Absolute:
                return operatorAbs(base.duplicate().getProcessor());

            case Average:
                return operatorAverage(base.duplicate().getProcessor(), (float) Math.abs(value));

            default: case Difference: case None: case All:
                return base;
        }
    }

    public ImagePlus applyOperator(Operator op, ImagePlus base, ImagePlus processI){

        switch(op){

            case Add:
                return operatorAdd(base.duplicate().getProcessor(), processI.getProcessor());

            case Subtract:
                return operatorSub(base.duplicate().getProcessor(), processI.getProcessor());

            case Multiply:
                return operatorMult(base.duplicate().getProcessor(), processI.getProcessor());

            case Divide:
                return operatorDiv(base.duplicate().getProcessor(), processI.getProcessor());

            case Min:
                return operatorMin(base.duplicate().getProcessor(), processI.getProcessor());

            case Max:
                return operatorMax(base.duplicate().getProcessor(), processI.getProcessor());

            case Average:
                return operatorAverage(base.duplicate().getProcessor(), processI.getProcessor());

            case Difference:
                return operatorDifference(base.duplicate().getProcessor(), processI.getProcessor());

            default: case None: case All: case Reverse: case AND: case OR: case XOR: case Log2: case Logarithm: case Exponential: case SquareRoot: case Gamma: case Absolute: case Square:
                return base;
        }
    }

        /***************
         * OperatorRev *
         ***************/
    private ImagePlus operatorRev(ImageProcessor image){

        image.invert();
        
        return new ImagePlus("rev", image);
    }

        /***************
         * OperatorAdd *
         ***************/
    private ImagePlus operatorAdd(ImageProcessor image, double value){

        image.add(value);
        
        return new ImagePlus("add_" + value, image);
    }

    private ImagePlus operatorAdd(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                float tmp = base[j][i] + proc[j][i];
                if((-255.0 <= tmp) && (tmp <= 255.0F)){

                    base[j][i] = tmp;
                }
                else{

                    base[j][i] = Math.signum(tmp)*255.0F;
                }
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("add_Image", image);
    }

        /***************
         * OperatorSub *
         ***************/
    private ImagePlus operatorSub(ImageProcessor image, double value){

        image.subtract(value);
        
        return new ImagePlus("sub_" + value, image);
    }

    private ImagePlus operatorSub(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                float tmp = base[j][i] - proc[j][i];
                if((-255.0 <= tmp) && (tmp <= 255.0F)){

                    base[j][i] = tmp;
                }
                else{

                    base[j][i] = Math.signum(tmp)*255.0F;
                }
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("sub_Image", image);
    }

        /****************
         * OperatorMult *
         ****************/
    private ImagePlus operatorMult(ImageProcessor image, double value){

        image.multiply(value);
        
        return new ImagePlus("mult_" + value, image);
    }

    private ImagePlus operatorMult(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                float tmp = base[j][i] * proc[j][i];
                if((-255.0 <= tmp) && (tmp <= 255.0F)){

                    base[j][i] = tmp;
                }
                else{

                    base[j][i] = Math.signum(tmp)*255.0F;
                }
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("mult_Image", image);
    }

        /***************
         * OperatorDiv *
         ***************/
    private ImagePlus operatorDiv(ImageProcessor image, double value){

        if(value == 0.0){

            value = 1.0;
        }

        image.multiply(1.0/value);

        return new ImagePlus("div_" + value, image);
    }

    private ImagePlus operatorDiv(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                float tmp = base[j][i]/proc[j][i];
                if((-255.0 <= tmp) && (tmp <= 255.0F)){

                    base[j][i] = tmp;
                }
                else{

                    base[j][i] = Math.signum(tmp)*255.0F;
                }
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("div_Image", image);
    }

        /***************
         * OperatorMin *
         ***************/
    private ImagePlus operatorMin(ImageProcessor image, double value){

        image.min(value);
        
        return new ImagePlus("min_" + value, image);
    }

    private ImagePlus operatorMin(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                base[j][i] = Math.min(base[j][i], proc[j][i]);
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("min_Image", image);
    }

        /***************
         * OperatorMax *
         ***************/
    private ImagePlus operatorMax(ImageProcessor image, double value){

        image.max(value);

        return new ImagePlus("max_" + value, image);
    }

    private ImagePlus operatorMax(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                base[j][i] = Math.max(base[j][i], proc[j][i]);
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("max_Image", image);
    }

        /***************
         * OperatorAnd *
         ***************/
    private ImagePlus operatorAnd(ImageProcessor image, int value){

        image.and(value);

        return new ImagePlus("AND_" + value, image);
    }

        /**************
         * OperatorOr *
         **************/
    private ImagePlus operatorOr(ImageProcessor image, int value){

        image.or(value);

        return new ImagePlus("OR_" + value, image);
    }

        /***************
         * OperatorXor *
         ***************/
    private ImagePlus operatorXor(ImageProcessor image, int value){

        image.xor(value);

        return new ImagePlus("XOR_" + value, image);
    }

        /*****************
         * OperatorGamma *
         *****************/
    private ImagePlus operatorGamma(ImageProcessor image, double value){

        image.gamma(value);

        return new ImagePlus("gamma_" + value, image);
    }

        /***************
         * OperatorLog *
         ***************/
    private ImagePlus operatorLog(ImageProcessor image){

        image.log();

        return new ImagePlus("log2", image);
    }

        /**************
         * OperatorLn *
         **************/
    private ImagePlus operatorLn(ImageProcessor image){

        image.ln();

        return new ImagePlus("ln", image);
    }

        /***************
         * OperatorExp *
         ***************/
    private ImagePlus operatorExp(ImageProcessor image){

        image.exp();

        return new ImagePlus("exp", image);
    }

        /***************
         * OperatorSqr *
         ***************/
    private ImagePlus operatorSqr(ImageProcessor image){

        image.sqr();

        return new ImagePlus("sqr", image);
    }

        /****************
         * OperatorSqrt *
         ****************/
    private ImagePlus operatorSqrt(ImageProcessor image){

        image.sqrt();

        return new ImagePlus("sqrt", image);
    }

        /***************
         * OperatorAbs *
         ***************/
    private ImagePlus operatorAbs(ImageProcessor image){

        image.abs();

        return new ImagePlus("Log", image);
    }

        /*******************
         * OperatorAverage *
         *******************/
    private ImagePlus operatorAverage(ImageProcessor image, float value){

        float[][] base = image.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                float tmp = (base[j][i] + value)/2.0F;
                if((-255.0 <= tmp) && (tmp <= 255.0F)){

                    base[j][i] = tmp;
                }
                else{

                    base[j][i] = Math.signum(tmp)*255.0F;
                }
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("aver_" + value, image);
    }

    private ImagePlus operatorAverage(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                float tmp = (base[j][i] + proc[j][i])/2.0F;
                if((-255.0 <= tmp) && (tmp <= 255.0F)){

                    base[j][i] = tmp;
                }
                else{

                    base[j][i] = Math.signum(tmp)*255.0F;
                }
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("aver_Image", image);
    }

        /**********************
        * OperatorDifference *
         **********************/
    private ImagePlus operatorDifference(ImageProcessor image, ImageProcessor processor){

        float[][] base = image.getFloatArray();
        float[][] proc = processor.getFloatArray();

        for(int j = 0; j < base.length; ++j){

            for(int i = 0; i < base[0].length; ++i){

                float tmp = Math.abs(base[j][i] - proc[j][i]);
                if((-255.0 <= tmp) && (tmp <= 255.0F)){

                    base[j][i] = tmp;
                }
                else{

                    base[j][i] = Math.signum(tmp)*255.0F;
                }
            }
        }

        image.setFloatArray(base);

        return new ImagePlus("diff_Image", image);
    }
}
