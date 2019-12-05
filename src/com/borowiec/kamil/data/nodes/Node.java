package com.borowiec.kamil.data.nodes;

import java.util.ArrayList;

public abstract class Node {
    private Node parent = null;
    private Node leftChild = null;
    private Node rightChild = null;
    private ArrayList<Node> parents;
    
    public Node() {
        parents = new ArrayList<>();
    }

    public Node getParentNode() {
        return parent;
    }

    public ArrayList<Node> getParentNodes() {
        return parents;
    }

    public void setParentNode(Node newParent) {
        parent = newParent;
        parents.add(newParent);
    }

    public Node getLeftChildNode() {
        return leftChild;
    }

    public void setLeftChildNode(Node newLChild) {
        leftChild = newLChild;
        leftChild.setParentNode(this);
    }

    public Node getRightChildNode() {
        return rightChild;
    }

    public void setRightChildNode(Node newRChild) {
        rightChild = newRChild;
        rightChild.setParentNode(this);
    }

    public void setChildren(Node left, Node right) {
        this.setLeftChildNode(left);
        this.setRightChildNode(right);
    }
}
