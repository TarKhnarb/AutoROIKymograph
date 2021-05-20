package Required;

public class Point{

    public final int x;
    public final int y;
    public Node<Point> current;

    public Point(int x, int y, Node<Point> current){

        this.x = x;
        this.y = y;
        this.current = current;
    }

    public void setCurrent(Node<Point> current){

        this.current = current;
    }
}
