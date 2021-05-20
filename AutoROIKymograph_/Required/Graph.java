package Required;

import java.util.ArrayList;

public class Graph<E>{

        /************
         * Variable *
         ************/
    private final Node<E> root;

        /***************
         * Constructor *
         ***************/
    public Graph(E rootValue){

        this.root = new Node<>(rootValue);
    }

        /************
         * AddChild *
         ************/
    public void addChild(E value){

        this.root.addchild(new Node<>(value));
    }

        /************
         * AddChild *
         ************/
    public void addChild(Node<E> node){

        this.root.addchild(node);
    }

        /***********
         * GetRoot *
         ***********/
    public Node<E> getRoot(){

        return this.root;
    }

        /****************
         * getTreeDepth *
         ****************/
    public int getTreeDepth(){

        ArrayList<ArrayList<Node<E>>> paths = this.getAllPaths();
        int max = 0;
        for(ArrayList<Node<E>> path : paths){

            if(max < path.size()){

                max = path.size();
            }
        }

        return max;
    }

        /***************
         * GetAllPaths *
         ***************/
    public ArrayList<ArrayList<Node<E>>> getAllPaths(){

        ArrayList<ArrayList<Node<E>>> all = new ArrayList<>();
        ArrayList<Node<E>> path = new ArrayList<>();
        getPath(this.root, path, all);

        return all;
    }


        /***********
         * GetPath *
         ***********/
    private void getPath(Node<E> node, ArrayList<Node<E>> path, ArrayList<ArrayList<Node<E>>> paths){

        if((node == null) || (path == null) || (paths == null)){

            return;
        }

        path.add(node);             // Add current node to the path
        if(!node.hasChildren()){    // The current node is a leaf

            paths.add(clone(path));
        }

        for(Node<E> n : node.getChildren()){   // Otherwise for each child get it's path

            getPath(n, path, paths);
        }

        int index = path.indexOf(node);
        if(path.size() > index){        // Clear the end of each path

            path.subList(index, path.size()).clear();
        }
    }


        /*********
         * Clone *
         *********/
    private ArrayList<Node<E>> clone(ArrayList<Node<E>> path){

        ArrayList<Node<E>> toReturn = new ArrayList<>();
        for(Node<E> n : path){

            toReturn.add(new Node<>(n));
        }

        return toReturn;
    }
}
