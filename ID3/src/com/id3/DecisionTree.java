package com.id3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import static com.id3.DataInstance.findBestAttribute;
import static com.id3.DataInstance.findEntropy;
import static com.id3.DataInstance.findRandomAttribute;

/**
 * Created by pavan on 2/7/17.
 */
public class DecisionTree {
    private static ArrayList<String> labels;
    private static int nodeCount;
    private static int randomNodeCount;
    private static int leafNodes;
    private static int randomLeafNodes;
    private static int nodeId;
    private static int sumDepth;
    private static int randSumDepth;

    public static void getData(ArrayList<DataInstance> data, String fileName) throws Exception{
        labels = new ArrayList<String>();
        String line="";
        int count = 0;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((line = br.readLine()) != null) {
            if(count==0){
                String[] values = line.split(",");
                for(int j=0; j<values.length;j++) {
                    labels.add(values[j]);
                }
                count++;
                continue;
            }
            DataInstance i = new DataInstance(line, labels);
            data.add(i);
        }
        br.close();
    }

    private static int findCount(ArrayList<DataInstance> instances, int type){
        int count=0;
        for(DataInstance instance: instances ){
            if(type == 0 && "0".equals(instance.details.get(labels.get(labels.size()-1)))){
                count++;
            }else if(type == 1 && "1".equals(instance.details.get(labels.get(labels.size()-1)))){
                count++;
            }
        }
        return count;
    }
    private static Nodes id3(Nodes parent, ArrayList<DataInstance> instances,ArrayList<String> labels, double entropy, boolean isLeft, int depth){
        ArrayList<DataInstance> leftInstances = new ArrayList<DataInstance>();
        ArrayList<DataInstance> rightInstances = new ArrayList<DataInstance>();
        ArrayList<String> remaining_labels = new ArrayList<String>(labels);
        String splitLabel = "";
        Nodes node = new Nodes();
        nodeCount++;
        nodeId++;
        depth++;
        node.setNegCount(findCount(instances,0));
        node.setPosCount(findCount(instances,1));
        node.setNodeId(nodeId);
        node.setParent(parent);
        node.setInstances(instances);
        node.setEntropy(entropy);
        if(isLeft && node.getParent()!=null){
            node.setLeft(true);
            node.setRight(false);
        }else if(!isLeft && node.getParent()!=null){
            node.setLeft(false);
            node.setRight(true);
        }
        node.setLeaf(false);
        node.setDepth(depth);
        entropy = findEntropy(instances,labels.get(labels.size()-1));
        //labels.remove(labels.size()-1);
        if(entropy>0 && remaining_labels.size()>1) {
            splitLabel = findBestAttribute(instances,remaining_labels);
            node.setSplitLabel(splitLabel);
            remaining_labels.remove(splitLabel);
            for(DataInstance instance : instances){
                if("0".equals(instance.details.get(splitLabel))){
                    leftInstances.add(instance);
                }else{
                    rightInstances.add(instance);
                }
            }
            if(leftInstances!=null) {
                node.setLeftNode(id3(node, leftInstances, remaining_labels, entropy, true,depth));
            }
            if(rightInstances!=null) {
                node.setRightNode(id3(node, rightInstances, remaining_labels, entropy, false,depth));
            }
        }else{
            node.setLeaf(true);
            leafNodes++;
            sumDepth += depth;
            if(node.getNegCount()>node.getPosCount())
                node.setSplitLabel("0");
            else
                node.setSplitLabel("1");
        }
        return node;
    }

    private static Nodes rand(Nodes parent, ArrayList<DataInstance> instances,ArrayList<String> labels, double entropy, boolean isLeft, int depth){
        ArrayList<DataInstance> leftInstances = new ArrayList<DataInstance>();
        ArrayList<DataInstance> rightInstances = new ArrayList<DataInstance>();
        ArrayList<String> remaining_labels = new ArrayList<String>(labels);
        String splitLabel = "";
        Nodes node = new Nodes();
        randomNodeCount++;
        depth++;
        node.setNegCount(findCount(instances,0));
        node.setPosCount(findCount(instances,1));
        node.setParent(parent);
        node.setInstances(instances);
        node.setDepth(depth);
        if(isLeft && node.getParent()!=null){
            node.setLeft(true);
            node.setRight(false);
        }else if(!isLeft && node.getParent()!=null){
            node.setLeft(false);
            node.setRight(true);
        }
        node.setLeaf(false);
        entropy = findEntropy(instances,labels.get(labels.size()-1));
        //labels.remove(labels.size()-1);
        if(entropy>0 && remaining_labels.size()>1) {
            splitLabel = findRandomAttribute(instances,remaining_labels);
            node.setSplitLabel(splitLabel);
            remaining_labels.remove(splitLabel);
            for(DataInstance instance : instances){
                if("0".equals(instance.details.get(splitLabel))){
                    leftInstances.add(instance);
                }else{
                    rightInstances.add(instance);
                }
            }
            if(leftInstances!=null) {
                node.setLeftNode(rand(node, leftInstances, remaining_labels, entropy, true,depth));
            }
            if(rightInstances!=null) {
                node.setRightNode(rand(node, rightInstances, remaining_labels, entropy, false,depth));
            }
        }else{
            node.setLeaf(true);
            randomLeafNodes++;
            randSumDepth += depth;
            if(node.getNegCount()>node.getPosCount())
                node.setSplitLabel("0");
            else
                node.setSplitLabel("1");
        }
        return node;
    }

    private static String classify(Nodes node, DataInstance instance){
        if(node.getLeftNode() == null && node.getRightNode() == null){
            return node.getSplitLabel();
        }
        if("0".equals(instance.details.get(node.getSplitLabel()))){
            return classify(node.getLeftNode(), instance);
        }else{
            return classify(node.getRightNode(),instance);
        }
    }

    private static double testData(ArrayList<DataInstance> testData, Nodes root){
        String predictedLabel;
        int success=0;
        int fail=0;
        for(DataInstance singleInstance : testData){
            predictedLabel = classify(root, singleInstance);
            if(predictedLabel.equals(singleInstance.details.get(labels.get(labels.size()-1)))){
                success++;
            }else {
                fail++;
            }
        }
        int total = success+fail;
        double val =(double) success/total;
        return val*100;
    }

    private static Nodes findByNodeId(Nodes node, int nodeId){
        Nodes temp = null;
        Queue<Nodes> queue = new LinkedList<Nodes>();
        queue.offer(node);
        while(!queue.isEmpty()) {
            temp = queue.poll();
            if(temp.getNodeId()==nodeId){
                break;
            } else{
                if(temp.getLeftNode()!= null)
                    queue.offer(temp.getLeftNode());
                if(temp.getRightNode()!=null)
                    queue.offer(temp.getRightNode());
            }
        }
        return temp;
    }

    public static void printTree(Nodes nodes, int depth){
        depth++;
        if("0".equals(nodes.getSplitLabel()) || "1".equals(nodes.getSplitLabel())){
            System.out.print(" : " + nodes.getSplitLabel());
        }
        else{
            System.out.println();
            for(int i=0; i<depth;i++){
                System.out.print(" | ");
            }
            System.out.print(nodes.getSplitLabel() + " = 0");
        }

        if(nodes.getLeftNode() != null){
            printTree(nodes.getLeftNode(),depth);
            if("0".equals(nodes.getSplitLabel()) || "1".equals(nodes.getSplitLabel())){
                System.out.print(" : " + nodes.getSplitLabel());
            }
            else{
                System.out.println();
                for(int i=0; i<depth;i++){
                    System.out.print(" | ");
                }
                System.out.print(nodes.getSplitLabel() + " = 1" );
            }
            printTree(nodes.getRightNode(),depth);
        }
        depth--;
    }

    public static void main(String[] args){
        try {
            ArrayList<DataInstance> training = new ArrayList<DataInstance>();
            ArrayList<DataInstance> testing = new ArrayList<DataInstance>();
            ArrayList<DataInstance> validation = new ArrayList<DataInstance>();
            ArrayList<DataInstance> leftInstances = new ArrayList<DataInstance>();
            ArrayList<DataInstance> rightInstances = new ArrayList<DataInstance>();
            getData(training, args[0]);
            getData(validation,args[1]);
            getData(testing,args[2]);
            double initialEntropy = findEntropy(training,labels.get(labels.size()-1)); // Find the initial entropy of the data.
            //System.out.println(initialEntropy);
            //System.out.println(findBestAttribute(training,labels));
            Nodes root = new Nodes();
            nodeCount++;
            nodeId++;
            String splitLabel = findBestAttribute(training,labels);
            root.setSplitLabel(splitLabel);
            root.setInstances(training);
            root.setNodeId(nodeId);
            root.setDepth(0);
            ArrayList<String> remLabels = new ArrayList<String>(labels);
            remLabels.remove(splitLabel);
            for(DataInstance instance : training){
                if("0".equals(instance.details.get(splitLabel))) {
                    leftInstances.add(instance);
                }else{
                    rightInstances.add(instance);
                }
            }
            int labelSize = labels.size()-1;
            root.setLeftNode(id3(root, leftInstances,remLabels,initialEntropy,true,0));
            root.setRightNode(id3(root, rightInstances,remLabels,initialEntropy, false,0));
            StringBuilder tree = new StringBuilder(1000);
            printTree(root,0);
            System.out.println("\nID3 Accuracy");
            System.out.println("------------------------------------");
            // training data
            System.out.println("Total number of training instances:"+training.size());
            System.out.println("Number of attributes:"+labelSize);
            System.out.println("Total number of Nodes:"+nodeCount);
            System.out.println("Sum of depth of Leaf Nodes:"+sumDepth);
            System.out.println("Average depth of ID3:"+(double)sumDepth/leafNodes);
            double accuracy = testData(training,root);
            System.out.println("Accuracy of the model on the training dataset:"+accuracy+"%");
            System.out.println("");

            //Random Generation
            Nodes root1 = new Nodes();
            randomNodeCount++;
            splitLabel = findRandomAttribute(training,labels);
            root1.setSplitLabel(splitLabel);
            root1.setInstances(training);
            root1.setDepth(0);
            remLabels = new ArrayList<String>(labels);
            remLabels.remove(splitLabel);
            for(DataInstance instance : training){
                if("0".equals(instance.details.get(splitLabel))) {
                    leftInstances.add(instance);
                }else{
                    rightInstances.add(instance);
                }
            }
            labelSize = labels.size()-1;
            root1.setLeftNode(rand(root1, leftInstances,remLabels,initialEntropy,true,0));
            root1.setRightNode(rand(root1, rightInstances,remLabels,initialEntropy, false,0));

            System.out.println("\nRandom Tree Accuracy");
            System.out.println("------------------------------------");
            // training data
            System.out.println("Total number of training instances:"+training.size());
            System.out.println("Number of attributes:"+labelSize);
            System.out.println("Total number of Nodes:"+randomNodeCount);
            System.out.println("Sum of the depth of leaf nodes"+randSumDepth);
            System.out.println("Average depth of Random Tree generation:"+(double)randSumDepth/randomLeafNodes);
            accuracy = testData(training,root1);
            System.out.println("Accuracy of the model on the training dataset:"+accuracy+"%");
            System.out.println("");
            printTree(root1,0);

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }
}
