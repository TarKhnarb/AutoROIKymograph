package Utilities;

public class Range{

    private final int min;
    private final int max;

    public Range(int min, int max){

        this.min = min;
        this.max = max;
    }

    public Range(Range range){

        this.min = range.min;
        this.max = range.max;
    }

    public int getMin(){

        return this.min;
    }

    public int getMax(){

        return this.max;
    }

    public int getRange(){

        return this.max - this.min;
    }

    public boolean belongToItself(int value){

        return ((min <= value) && (value <= max));
    }
}
