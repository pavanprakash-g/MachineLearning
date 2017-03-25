package com.id3;

import java.util.ArrayList;

/**
 * Created by pavan on 2/7/17.
 */
public class Nodes {
    private Nodes parent;
    private Nodes leftNode;
    private Nodes rightNode;
    private double entropy;
    private String splitLabel;
    private ArrayList<DataInstance> instances;
    private int posCount;
    private int negCount;
    private int nodeId;
    private int depth;
    private boolean isLeaf;
    private boolean isLeft;
    private boolean isRight;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getPosCount() {
        return posCount;
    }

    public void setPosCount(int posCount) {
        this.posCount = posCount;
    }

    public int getNegCount() {
        return negCount;
    }

    public void setNegCount(int negCount) {
        this.negCount = negCount;
    }

    public Nodes getParent() {
        return parent;
    }

    public void setParent(Nodes parent) {
        this.parent = parent;
    }

    public Nodes getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Nodes leftNode) {
        this.leftNode = leftNode;
    }

    public Nodes getRightNode() {
        return rightNode;
    }

    public void setRightNode(Nodes rightNode) {
        this.rightNode = rightNode;
    }

    public double getEntropy() {
        return entropy;
    }

    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }

    public String getSplitLabel() {
        return splitLabel;
    }

    public void setSplitLabel(String splitLabel) {
        this.splitLabel = splitLabel;
        //System.out.println(splitLabel);
    }

    public ArrayList<DataInstance> getInstances() {
        return instances;
    }

    public void setInstances(ArrayList<DataInstance> instances) {
        this.instances = instances;
    }
}
