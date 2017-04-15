import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by pavan on 4/14/17.
 */
public class KMeans {
    private ArrayList<Clusters> clustersList = new ArrayList<>();
    private ArrayList<Data> dataList = new ArrayList<>();

    public void getData(String fileName) throws Exception{
        String line="";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((line = br.readLine()) != null) {
            String[] values = line.split("\t");
            if("id".equals(values[0])){
                continue;
            }else {
                Data data = new Data();
                data.id = Integer.parseInt(values[0]);
                data.x = Float.parseFloat(values[1]);
                data.y = Float.parseFloat(values[2]);
                dataList.add(data);
            }
        }
        br.close();
    }

    public float generateRandomValue(){
        float minX = 0.0f;
        float maxX = 1.0f;
        Random rand = new Random();
        //return rand.nextFloat() * (maxX - minX) + minX;
        return 0;
    }

    public void generateRandomCenters(int k){
        while (k>0){
            Data data = new Data();
            Clusters cluster = new Clusters();
            data.x = generateRandomValue();
            data.y = generateRandomValue();
            cluster.center = data;
            cluster.id = k;
            clustersList.add(cluster);
            k--;
        }
    }

    private float findDistance(Data point1, Data point2){
        return (float)Math.sqrt(Math.pow(point1.x-point2.x,2)+Math.pow(point1.y-point2.y,2));
    }

    private void assignmentStep(){
        float distance;
        float minDistance = 100;
        Clusters finalCluster = new Clusters();
        for(Clusters cluster : clustersList){
            cluster.clusterData.clear();
        }
        for(Data data : dataList){
            for(Clusters cluster : clustersList){
                distance = findDistance(data,cluster.center);
                if(distance < minDistance){
                    minDistance = distance;
                    finalCluster = cluster;
                }

            }
            finalCluster.clusterData.add(data);
            minDistance = 100;
        }
    }

    private boolean updateStep(){
        float sumX;
        float sumY;
        float newX;
        float newY;
        boolean centersChanged = false;
        for(Clusters cluster : clustersList){
            sumX = 0;
            sumY = 0;
            newX = 0;
            newY = 0;
            for(Data data : cluster.clusterData){
                sumX += data.x;
                sumY += data.y;
            }
            if(cluster.clusterData.size() > 0) {
                newX = sumX /cluster.clusterData.size();
                newY = sumY / cluster.clusterData.size();
            }
            if(cluster.center.x != newX || cluster.center.y != newY) {
                cluster.center.x = sumX / cluster.clusterData.size();
                cluster.center.y = sumY / cluster.clusterData.size();
                if(!centersChanged){
                    centersChanged = true;
                }
            }

        }
        return centersChanged;
    }

    public void applyKMeans(){
        boolean centersChanged = true;
        while (centersChanged){
            assignmentStep();
            centersChanged = updateStep();
        }
    }

    public float calculateSSE(){
        float totalSSE = 0;
        float clusterSSE;
        for(Clusters clusters : clustersList){
            clusterSSE = 0;
            for(Data data : clusters.clusterData){
                clusterSSE += Math.pow(findDistance(data,clusters.center),2);
            }
            totalSSE+=clusterSSE;
        }
        return totalSSE;
    }

    public void writeOutput(String outputPath, float SSE){
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            String content = "ClusterId \tId of the Point \n";
            content +="-------------------------------------------------------------\n";
            fw = new FileWriter(outputPath);
            bw = new BufferedWriter(fw);
            for(Clusters cluster : clustersList){
                content += cluster.id+"\t\t";
                for(Data data : cluster.clusterData){
                    content += data.id+",";
                }
                content = content.substring(0,content.lastIndexOf(",")) + "\n\n";
            }
            content +="\n-------------------------------------------------------------\n";
            content += "SSE = "+SSE;
            bw.write(content);
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();

            }

        }
    }

}
