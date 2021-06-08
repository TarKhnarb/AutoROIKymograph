package Required;

public class Range<E extends Number>{

        /*************
         * Variables *
         *************/
    private E min;
    private E max;

        /****************
         * Constructors *
         ****************/
    public Range(E min, E max){

        this.min = min;
        this.max = max;
    }

    public Range(Range<E> range){

        this.min = range.getMin();
        this.max = range.getMax();
    }

    public void setMin(E min){

        this.min = min;
    }

        /**********
         * GetMin *
         **********/
    public E getMin(){

        return this.min;
    }

    public void setMax(E max){

        this.max = max;
    }

        /**********
         * GetMax *
         **********/
    public E getMax(){

        return this.max;
    }

    public void setRange(E min, E max){

        this.min = min;
        this.max = max;
    }

        /************
         * GetRange *
         ************/
    public double getRange(){

        return this.max.doubleValue() - this.min.doubleValue();
    }

        /******************
         * BelongToItself *
         ******************/
    public boolean belongToItself(int value){

        return ((this.min.intValue() <= value) && (value <= this.max.intValue()));
    }

    public boolean belongToItself(double value){

        return ((this.min.doubleValue() <= value) && (value <= this.max.doubleValue()));
    }
}
