import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pavan on 3/6/17.
 */
public class PreProcessing {

    private static boolean isFloat(String data){
        boolean isFloat = false;
        try{
            Float.parseFloat(data);
            isFloat = true;
        }catch (Exception e){
        }
        return isFloat;
    }

    private static boolean isBoolean(ArrayList<DataInstance> dataInstances, int index){
        int count = 1;
        Map<String, Integer> attributeValues = new HashMap<>();
        for(DataInstance singleInstance: dataInstances){
            if(attributeValues.get(singleInstance.details.get(index)) == null) {
                attributeValues.put(singleInstance.details.get(index), count);
                count++;
            }

        }
        if(count > 2){
            return false;
        }
        return true;
    }

    private static void convertType(ArrayList<DataInstance> dataInstances, int index){
        int count = 1;
        Map<String, Integer> attributeValues = new HashMap<>();
        for(DataInstance singleInstance: dataInstances){
            if(attributeValues.get(singleInstance.details.get(index)) == null) {
                attributeValues.put(singleInstance.details.get(index), count);
                count++;
            }

        }
        for (DataInstance singleInstance : dataInstances) {
            singleInstance.details.replace(index, ""+attributeValues.get(singleInstance.details.get(index)));
        }
    }

    private static void generatePreprocessedFile(ArrayList<DataInstance> dataInstances, String filePath){
        BufferedWriter bw = null;
        FileWriter fw = null;
        String content = "";
        String newPath = "";
        try {
            for (DataInstance instance : dataInstances) {
                /*for (int i = 1; i <= instance.details.size(); i++) {
                    content += instance.details.get(i)+" ";
                }*/
                content = content.concat(instance.details.toString() +"\n");
                //content += "\n";
            }
            newPath = filePath.substring(0,filePath.lastIndexOf("/"));
            newPath = newPath + "/preprocessed.data";
            fw = new FileWriter(newPath);
            bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();
        }catch (Exception e){

        }finally {
        }
    }

    private static void standardize(ArrayList<DataInstance> dataInstances, int index, int instanceCount){
        float sum =(float) 0.0;
        float currentValue;
        float mean;
        float variance = (float)0.0;
        float standardDeviation;
        float updatedValue;
        for(DataInstance singleInstance : dataInstances){
            //System.out.println(singleInstance.details.toString());
            sum += Float.parseFloat(singleInstance.details.get(index));
        }

        if(isBoolean(dataInstances, index)){
            return;
        }

        mean = sum/instanceCount;
        //variance
        for(DataInstance singleInstance : dataInstances){
            currentValue = Float.parseFloat(singleInstance.details.get(index));
            variance += Math.pow(currentValue-mean,2);
        }
        variance = variance/instanceCount;
        standardDeviation = (float)Math.sqrt(variance);
        //Standardization
        for(DataInstance singleInstance : dataInstances) {
            currentValue = Float.parseFloat(singleInstance.details.get(index));
            updatedValue = (currentValue - mean) / standardDeviation;
            singleInstance.details.replace(index, "" + updatedValue);
        }
    }

    public static void preprocessData(ArrayList<DataInstance> dataInstances, String dataPath, ArrayList<Integer> indexes, int instanceCount){
        for(int index: indexes){
            if(isFloat(dataInstances.get(0).details.get(index))){
                standardize(dataInstances, index, instanceCount);
            }else{
                convertType(dataInstances, index);
            }
        }
        generatePreprocessedFile(dataInstances, dataPath);
    }
}
