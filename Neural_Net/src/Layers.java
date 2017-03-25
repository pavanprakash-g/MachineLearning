/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

import java.util.ArrayList;

/**
 * Created by pavan on 3/1/17.
 */
public class Layers {
    int layerId;
    int numOfNodes;
    boolean isInput;
    boolean isHidden;
    boolean isOutput;
    ArrayList<Neurons> neurons = new ArrayList<>();

    Layers(boolean isInput, boolean isHidden, boolean isOutput, int layerId){
        this.isInput = isInput;
        this.isHidden = isHidden;
        this.isOutput = isOutput;
        this.layerId = layerId;
    }

    public ArrayList<Neurons> getNeurons() {
        return neurons;
    }

    public void setNeurons(ArrayList<Neurons> neurons) {
        this.neurons = neurons;
    }

    public int getLayerId() {
        return layerId;
    }

    public void setLayerId(int layerId) {
        this.layerId = layerId;
    }

    public boolean isInput() {
        return isInput;
    }

    public void setInput(boolean input) {
        isInput = input;
    }

    public int getNumOfNodes() {
        return numOfNodes;
    }

    public void setNumOfNodes(int numOfNodes) {
        this.numOfNodes = numOfNodes;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    }
}
