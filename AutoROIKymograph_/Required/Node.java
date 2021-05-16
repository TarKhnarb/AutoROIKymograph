package Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node<E>{

        /*************
         * Variables *
         *************/
    private Node<E> parent;
    private List<Node<E>> children;
    private final E value;

        /****************
         * Constructors *
         ****************/
    public Node(E value){

        this.children = new ArrayList<>();
        this.value = value;
    }

    public Node(Node<E> node){

        this.value = node.getValue();
        this.parent = node.getParent();

        if(node.hasChildren()){

            setChildren(node.getChildren());
        }
    }

        /************
         * AddChild *
         ************/
    public void addchild(Node<E> node){

        node.setParent(this);
        this.children.add(node);
    }

        /**************
         * AddChildAt *
         **************/
    public void addChildAt(int index, Node<E> node){

        node.setParent(this);
        this.children.add(index, node);
    }

        /***************
         * AddChildren *
         ***************/
    public void addChildren(List<Node<E>> children){

        for(Node<E> n : children){

            n.setParent(this);
            this.children.add(n);
        }
    }

        /***************
         * RemoveChild *
         ***************/
    public void removeChild(int index){

        this.children.remove(index);
    }

        /************
         * GetValue *
         *************/
    public E getValue(){

        return this.value;
    }

        /***************
         * SetChildren *
         ***************/
    public void setChildren(List<Node<E>> children){

        clearChildren();
        for(Node<E> n : children){

            n.setParent(this);
        }

        this.children.addAll(children);
    }

        /****************
         * GetChildren *
         ****************/
    public List<Node<E>> getChildren(){

        return this.children;
    }

        /************
         * GetChild *
         ************/
    public Node<E> getChild(int index){

        if(index < this.children.size()){

            return this.children.get(index);
        }
        else{

            return null;
        }
    }

        /*************
         * SetParent *
         *************/
    public void setParent(Node<E> node){

        this.parent = node;
    }

        /*************
         * GetParent *
         *************/
    public Node<E> getParent(){

        return this.parent;
    }

        /*****************
         * ClearChildren *
         *****************/
    public void clearChildren(){

        if(this.children != null){

            if (!this.children.isEmpty()) {

                this.children.clear();
            }
        }

        this.children = new ArrayList<>();
    }

        /***************
         * HasChildren *
         ***************/
    public boolean hasChildren(){

        return !this.children.isEmpty();
    }

        /**********
         * Equals *
         **********/
    @Override
    public boolean equals(Object obj){

        if(!(obj instanceof Node)){

            return false;
        }

        if(this == obj){

            return true;
        }

        @SuppressWarnings("unchecked")  // Type already checked, just to remove unnecessary warning
        Node<E> node = (Required.Node<E>) obj;

        if(this.value != node.value){

            return false;
        }

        if(!Objects.equals(this.parent, node.parent)){

            return false;
        }

        return Objects.equals(this.children, node.children);
    }

        /***********
         * HasCode *
         ***********/
    @Override
    public int hashCode(){

        int result = (this.parent != null ? this.parent.hashCode() : 0);

        result = 31 * result + (this.children != null ? this.children.hashCode() : 0);
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);

        return result;
    }
}