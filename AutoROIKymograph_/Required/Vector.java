package Required;

public class Vector{

        /*************
         * Variables *
         *************/
    public final Coord a;
    public final Coord b;
    public final Coord v;
    public final float aver;
    public Node<Vector> itself;

        /****************
         * Constructors *
         ****************/
    public Vector(Coord a, Coord b, float aValue, float bValue, Node<Vector> itself){

        this.a = a;
        this.b = b;
        this.v = new Coord(b.x - a.x, b.y - a.y);
        this.aver = (aValue + bValue)/2.0f;
        this.itself = itself;
    }

        /*************
         * SetItself *
         *************/
    public void setItself(Node<Vector> itself){

        this.itself = itself;
    }

        /****************
         * IsPositiveVx *
         ****************/
    public boolean isPositiveVx(){

        return this.v.x >= 0;
    }

        /****************
         * IsPositiveVy *
         ****************/
    public boolean isPositiveVy(){

        return this.v.y >= 0;
    }

        /**********
         * Equals *
         **********/
    @Override
    public boolean equals(Object obj){

        if(obj == null){

            return false;
        }

        if(!(obj instanceof Vector)){

            return false;
        }

        if(this == obj){

            return true;
        }

        Vector vector = (Vector) obj;

        return v.equals(vector.v);
    }
}
