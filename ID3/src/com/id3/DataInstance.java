package com.id3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by pavan on 2/7/17.
 */
public class DataInstance {
    HashMap<String,String> details = new HashMap<String, String>();

    DataInstance(String singleRow, ArrayList<String> labels){
        String[] data = singleRow.split(",");
        for(int i=0; i< data.length; i++) {
            details.put(labels.get(i), data[i]);
        }
    }

    public static double findEntropy(ArrayList<DataInstance> data, String label){
        int positiveCount = 0;
        int totalCount = 0;
        for (DataInstance single: data) {
            //System.out.println("Label"+label);
            //System.out.println("Value"+single.details.get(label));
            if("1".equals(single.details.get(label))){
                positiveCount++;
            }
            totalCount++;
        }
        int negativeCount = totalCount - positiveCount;
        //System.out.println("totalCount::"+totalCount+" positiveCount:::"+positiveCount);
        double p1 = (double)positiveCount/totalCount;
        double p2 = (double)negativeCount/totalCount;
        double logp1 = p1==0?0:Math.log(p1);
        double logp2 = p2==0?0:Math.log(p2);
        return  (-p1*(logp1/Math.log(2)))+ (-p2*(logp2/Math.log(2)));
    }

    public static String findRandomAttribute(ArrayList<DataInstance> dataInstances, ArrayList<String> labels){
        int min = 1;
        int max = labels.size();
        Random rand = new Random();
        return labels.get(rand.nextInt((max - min)));
    }

    public static String findBestAttribute(ArrayList<DataInstance> data, ArrayList<String> labels){
        int positiveCount = 0;
        int totalCount = 0;
        int r1posCount = 0;
        int r2posCount = 0;
        int r1negCount = 0;
        int r2negCount = 0;
        int r1totalCount = 0;
        int r2totalCount = 0;
        double p1 = 0.0;
        double p2 = 0.0;
        double entropy1 = 0.0;
        double entropy2 = 0.0;
        double entropy = 0.0;
        double IG = 0.0;
        double maxIG = 0.0;
        int negativeCount=0;
        double logp1 = 0.0;
        double logp2 = 0.0;
        String nodeLabel = "";
        String lastLabel = labels.get(labels.size()-1);
        labels.remove(labels.size()-1);
        double totalEntropy = findEntropy(data, lastLabel);
        for(String singleLabel : labels ) {
            for (DataInstance single : data) {
                if ("1".equals(single.details.get(singleLabel))) {
                    if("1".equals(single.details.get(lastLabel))){
                        r1posCount++;
                    }else{
                        r1negCount++;
                    }
                    r1totalCount++;
                }else {
                    if("1".equals(single.details.get(lastLabel))){
                        r2posCount++;
                    }else{
                        r2negCount++;
                    }
                    r2totalCount++;
                }
            }
            totalCount = r1totalCount + r2totalCount;
            p1 = r1totalCount!=0? (double) r1posCount / r1totalCount : 0;
            p2 = r1totalCount!=0? (double) r1negCount / r1totalCount : 0;
            logp1 = p1==0?0:Math.log(p1);
            logp2 = p2==0?0:Math.log(p2);
            entropy1 = (-p1 * (logp1 / Math.log(2))) + (-p2 * (logp2 / Math.log(2)));
            p1 = r2totalCount!=0? (double) r2posCount / r2totalCount : 0;
            p2 = r2totalCount!=0? (double) r2negCount / r2totalCount : 0;
            logp1 = p1==0?0:Math.log(p1);
            logp2 = p2==0?0:Math.log(p2);
            entropy2 = (-p1 * (logp1 / Math.log(2))) + (-p2 * (logp2 / Math.log(2)));
            p1 = (double) r1totalCount/totalCount;
            p2 = (double) r2totalCount/totalCount;
            entropy = (p1*entropy1+p2*entropy2);
            IG = totalEntropy - entropy;
            if(IG >= maxIG) {
                maxIG = IG;
                nodeLabel = singleLabel;
            }
            r1posCount = 0;
            r2posCount = 0;
            r1negCount = 0;
            r2negCount = 0;
            r1totalCount = 0;
            r2totalCount = 0;
        }
        labels.add(lastLabel);
        return nodeLabel;
    }
}
