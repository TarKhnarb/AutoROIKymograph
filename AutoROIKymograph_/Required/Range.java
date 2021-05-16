package Required;

public class Range<E extends Number>{

        /*************
         * Variables *
         *************/
    private final E min;
    private final E max;

        /****************
         * Constructors *
         ****************/
    public Range(E min, E max){

        this.min = min;
        this.max = max;
    }

    public Range(Range<E> range){

        this.min = range.min;
        this.max = range.max;
    }

        /**********
         * GetMin *
         **********/
    public E getMin(){

        return this.min;
    }

        /**********
         * GetMax *
         **********/
    public E getMax(){

        return this.max;
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

        return ((min.intValue() <= value) && (value <= max.intValue()));
    }

    public boolean belongToItself(double value){

        return ((min.doubleValue() <= value) && (value <= max.doubleValue()));
    }
}
