package Required;

public class Coord{

        /*************
         * Variables *
         *************/
    public int x;
    public int y;

        /****************
         * Constructors *
         ****************/
    public Coord(int x, int y){

        this.x = x;
        this.y = y;
    }

    public Coord(Coord a){

        this.x = a.x;
        this.y = a.y;
    }

        /**********
         * Equals *
         **********/
    @Override
    public boolean equals(Object obj){

        if(obj == null){

            return false;
        }

        if(!(obj instanceof Coord)){

            return false;
        }

        if(this == obj){

            return true;
        }

        Coord coord = (Coord) obj;

        if(x != coord.x){

            return false;
        }

        return y == coord.y;
    }
}
